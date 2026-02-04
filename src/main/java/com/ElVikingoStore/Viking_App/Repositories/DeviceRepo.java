package com.ElVikingoStore.Viking_App.Repositories;

import java.util.List;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ElVikingoStore.Viking_App.Models.User;
import com.ElVikingoStore.Viking_App.Models.Device;

@Repository
public interface DeviceRepo extends JpaRepository<Device, UUID> {
        @Operation(summary = "Buscar dispositivo por número de serie")
        @ApiResponse(responseCode = "200", description = "Dispositivo encontrado")
        @ApiResponse(responseCode = "404", description = "Dispositivo no encontrado")
        Device findBySerialNumber(String serialNumber);

        @Operation(summary = "Buscar dispositivos por marca")
        @ApiResponse(responseCode = "200", description = "Lista de dispositivos encontrados")
        List<Device> findByBrand(
                        @Parameter(description = "Marca a buscar", example = "Samsung", required = true) String brand);

        @Operation(summary = "Buscar dispositivos por usuario")
        @ApiResponse(responseCode = "200", description = "Lista de dispositivos del usuario")
        List<Device> findByUser(
                        @Parameter(description = "Usuario propietario", required = true) User user);

}
