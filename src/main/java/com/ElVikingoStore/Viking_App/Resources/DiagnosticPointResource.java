package com.ElVikingoStore.Viking_App.Resources;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ElVikingoStore.Viking_App.DTOs.DiagnosticPoint.DiagnosticPointRequestDto;
import com.ElVikingoStore.Viking_App.DTOs.DiagnosticPoint.DiagnosticPointResponseDto;
import com.ElVikingoStore.Viking_App.DTOs.WorkOrder.WorkOrderResponseDto;
import com.ElVikingoStore.Viking_App.Models.DiagnosticPoint;
import com.ElVikingoStore.Viking_App.Models.WorkOrder;
import com.ElVikingoStore.Viking_App.Repositories.StorageInterface;
import com.ElVikingoStore.Viking_App.Services.DiagnosticPointService;
import com.ElVikingoStore.Viking_App.Services.WorkOrderService;

@Tag(name = "DiagnosticPoint", description = "Endpoints para la gestión de puntos de diagnóstico")
@RestController
@RequestMapping("/api/diagnostic-points")
public class DiagnosticPointResource {

    @Autowired
    private DiagnosticPointService diagnosticPointService;

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private StorageInterface storageService;

    // Nueva variable para almacenar la dirección IP
    @Value("${server.address}")
    private String serverAddress;
    @Value("${server.port}")
    private String serverPort;

    @Operation(summary = "Agregar punto de diagnóstico", description = "Agrega un nuevo punto de diagnóstico a una orden de trabajo")
    @PostMapping(value = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> addDiagnosticPoint(
            @RequestPart("diagnosticPoint") String diagnosticPointJson,
            @RequestPart("file") MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Support for LocalDateTime
            DiagnosticPointRequestDto requestDto = objectMapper.readValue(diagnosticPointJson,
                    DiagnosticPointRequestDto.class);

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Archivo multimedia no recibido");
            }
            if (requestDto.getWorkOrderId() == null) {
                return ResponseEntity.badRequest().body("WorkOrder no especificado o inválido");
            }
            WorkOrder workOrderEntity = workOrderService.getWorkOrderEntityById(requestDto.getWorkOrderId());
            if (workOrderEntity == null) {
                return ResponseEntity.badRequest().body("WorkOrder no encontrado");
            }

            DiagnosticPoint diagnosticPoint = new DiagnosticPoint();
            diagnosticPoint.setWorkOrder(workOrderEntity);
            diagnosticPoint.setDescription(requestDto.getDescription());
            diagnosticPoint.setNotes(requestDto.getNotes());
            if (requestDto.getTimestamp() != null) {
                diagnosticPoint.setTimestamp(requestDto.getTimestamp());
            }

            String filename = storageService.store(file);
            // Cambia la URL para que utilice el formato solicitado
            String url = "http://" + serverAddress + ":" + serverPort + "/auth/uploads/" + filename;
            diagnosticPoint.getMultimediaFiles().add(url);

            DiagnosticPoint savedDiagnosticPoint = diagnosticPointService.addDiagnosticPoint(diagnosticPoint);

            return ResponseEntity.ok(mapToResponseDto(savedDiagnosticPoint));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error al agregar el punto de diagnóstico: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener puntos de diagnóstico por ID de orden de trabajo", description = "Obtiene una lista de puntos de diagnóstico por ID de orden de trabajo y Cliente Asociado a la orden de trabajo")
    @GetMapping("/by-work-order/{workOrderId}/client/{clientId}")
    public ResponseEntity<?> getDiagnosticPointsByWorkOrderAndClient(
            @PathVariable UUID workOrderId,
            @PathVariable UUID clientId) {
        WorkOrderResponseDto workOrder = workOrderService.getWorkOrderById(workOrderId);
        if (workOrder == null || !workOrder.getClientId().equals(clientId)) {
            return ResponseEntity.badRequest().body("WorkOrder no encontrado o ID del cliente no coincide");
        }

        List<DiagnosticPoint> diagnosticPoints = diagnosticPointService
                .getDiagnosticPointsByWorkOrderId(workOrderId);

        List<DiagnosticPointResponseDto> responseDtos = diagnosticPoints.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    private DiagnosticPointResponseDto mapToResponseDto(DiagnosticPoint entity) {
        return DiagnosticPointResponseDto.builder()
                .id(entity.getId())
                .workOrderId(entity.getWorkOrder().getId())
                .timestamp(entity.getTimestamp())
                .description(entity.getDescription())
                .notes(entity.getNotes())
                .multimediaFiles(entity.getMultimediaFiles())
                .build();
    }
}
