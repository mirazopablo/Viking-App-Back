package com.ElVikingoStore.Viking_App.Resources;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.ElVikingoStore.Viking_App.DTOs.Device.DeviceCreateRequestDto;
import com.ElVikingoStore.Viking_App.DTOs.Device.DeviceResponseDto;
import com.ElVikingoStore.Viking_App.DTOs.Device.DeviceUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ElVikingoStore.Viking_App.Services.DeviceService;
import com.ElVikingoStore.Viking_App.Services.UserService;

@Tag(name = "Device Controller", description = "API para la gestión de dispositivos")
@RestController
@RequestMapping("/api/device")
public class DeviceResource {

    @Autowired
    DeviceService deviceService;

    @Autowired
    UserService userService;

    @Operation(summary = "Buscar Dispositivos", description = "Busca dispositivos en la base de datos")
    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchDevice(@RequestParam(required = false) UUID id,
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer userDni,
            @RequestParam(required = false) String query) {
        try {
            if (query == null) {
                return ResponseEntity.badRequest().body("Query parameter is required");
            }

            switch (query.toLowerCase()) {
                case "all" -> {
                    List<DeviceResponseDto> devices = deviceService.getAllDevices();
                    return ResponseEntity.ok(devices);
                }
                case "by-id" -> {
                    if (id == null) {
                        return ResponseEntity.badRequest().body("ID is required for 'by-id' query");
                    }
                    DeviceResponseDto deviceById = deviceService.getDeviceById(id);
                    return ResponseEntity.ok(deviceById);
                }
                case "by-serial-number" -> {
                    if (serialNumber == null) {
                        return ResponseEntity.badRequest()
                                .body("Serial number is required for 'by-serial-number' query");
                    }
                    DeviceResponseDto deviceBySerialNumber = deviceService.getDeviceBySerialNumber(serialNumber);
                    return ResponseEntity.ok(deviceBySerialNumber);
                }
                case "by-brand" -> {
                    if (brand == null) {
                        return ResponseEntity.badRequest().body("Brand is required for 'by-brand' query");
                    }
                    List<DeviceResponseDto> devicesByBrand = deviceService.getDevicesByBrand(brand);
                    return ResponseEntity.ok(devicesByBrand);
                }
                case "by-user-dni" -> {
                    if (userDni == null) {
                        return ResponseEntity.badRequest().body("User DNI is required for 'by-user-dni' query");
                    }
                    List<DeviceResponseDto> devicesByUser = deviceService.getDevicesByUserDni(userDni);
                    return ResponseEntity.ok(devicesByUser);
                }
                default -> {
                    return ResponseEntity.badRequest().body("Invalid query parameter");
                }
            }
        } catch (NoSuchElementException | NumberFormatException e) {
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }

    @Operation(summary = "Guardar Dispositivos", description = "Guarda un dispositivo en la base de datos")
    @PostMapping("/save")
    public ResponseEntity<DeviceResponseDto> registerDevice(@Valid @RequestBody DeviceCreateRequestDto deviceDto) {
        DeviceResponseDto response = deviceService.saveDevice(deviceDto);
        return ResponseEntity.ok(response);

    }

    @Operation(summary = "Actualizar Dispositivo", description = "Actualiza un dispositivo existente")
    @PutMapping("/update/{id}")
    public ResponseEntity<DeviceResponseDto> updateDevice(@PathVariable UUID id,
            @Valid @RequestBody DeviceUpdateRequestDto deviceDto) {
        try {
            if (!id.equals(deviceDto.getId())) {
                return ResponseEntity.badRequest().build();
            }

            DeviceResponseDto updatedDevice = deviceService.updateDevice(deviceDto);
            if (updatedDevice != null) {
                return ResponseEntity.ok(updatedDevice);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Eliminar Dispositivo", description = "Elimina un dispositivo existente")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable UUID id) {
        try {
            // Delegating logic to service layer which handles "not found" internally or
            // throw exception
            deviceService.deleteDevice(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting device: " + e.getMessage());
        }
    }
}
