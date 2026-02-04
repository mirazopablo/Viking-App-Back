package com.ElVikingoStore.Viking_App.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ElVikingoStore.Viking_App.Models.DiagnosticPoint;
import com.ElVikingoStore.Viking_App.Models.WorkOrder;
import com.ElVikingoStore.Viking_App.Repositories.DiagnosticPointRepo;
import com.ElVikingoStore.Viking_App.Repositories.WorkOrderRepo;

@Schema(name = "DiagnosticPointService", description = "Servicio para la gestión de puntos de diagnóstico")
@Service
public class DiagnosticPointService {

    private final DiagnosticPointRepo diagnosticPointRepo;
    private final WorkOrderRepo workOrderRepo;

    public DiagnosticPointService(DiagnosticPointRepo diagnosticPointRepo, WorkOrderRepo workOrderRepo) {
        this.diagnosticPointRepo = diagnosticPointRepo;
        this.workOrderRepo = workOrderRepo; // Asegúrate de que se inyecte aquí correctamente
    }

    @Operation(summary = "Agregar punto de diagnóstico", description = "Agrega un nuevo punto de diagnóstico a una orden de trabajo")
    public DiagnosticPoint addDiagnosticPoint(DiagnosticPoint diagnosticPoint) {
        // Verifica si el WorkOrder existe
        WorkOrder workOrder = workOrderRepo.findById(Objects.requireNonNull(diagnosticPoint.getWorkOrder().getId()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid work order ID"));

        diagnosticPoint.setWorkOrder(workOrder);
        return diagnosticPointRepo.save(diagnosticPoint);
    }

    @Operation(summary = "Obtener puntos de diagnóstico por ID de orden de trabajo", description = "Obtiene una lista de puntos de diagnóstico por ID de orden de trabajo")
    public List<DiagnosticPoint> getDiagnosticPointsByWorkOrderId(UUID workOrderId) {
        return diagnosticPointRepo.findByWorkOrder_Id(workOrderId);
    }

    @Operation(summary = "Obtener punto de diagnóstico por ID", description = "Obtiene un punto de diagnóstico por su ID")
    @Transactional(readOnly = true) // Asegura que haya una sesión activa para la carga
    public DiagnosticPoint findById(UUID id) {
        // Encuentra el DiagnosticPoint por su ID
        DiagnosticPoint diagnosticPoint = diagnosticPointRepo.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new IllegalArgumentException("DiagnosticPoint not found for ID: " + id));

        // Comprobar e inicializar multimediaFiles si es necesario
        if (diagnosticPoint.getMultimediaFiles() == null) {
            diagnosticPoint.setMultimediaFiles(new ArrayList<>());
        }

        return diagnosticPoint;
    }
}
