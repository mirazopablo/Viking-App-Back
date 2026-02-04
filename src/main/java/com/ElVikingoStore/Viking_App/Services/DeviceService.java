package com.ElVikingoStore.Viking_App.Services;

import java.util.List;
import java.util.Objects;

import java.util.UUID;

import com.ElVikingoStore.Viking_App.DTOs.Device.DeviceCreateRequestDto;
import com.ElVikingoStore.Viking_App.DTOs.Device.DeviceResponseDto;
import com.ElVikingoStore.Viking_App.DTOs.Device.DeviceUpdateRequestDto;
import com.ElVikingoStore.Viking_App.Repositories.UserRepo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ElVikingoStore.Viking_App.Models.User;
import com.ElVikingoStore.Viking_App.Models.Device;
import com.ElVikingoStore.Viking_App.Repositories.DeviceRepo;

import jakarta.transaction.Transactional;

@Schema(name = "DeviceService", description = "Servicio para la gestión de dispositivos")
@Service
@Transactional
public class DeviceService {

    @Autowired
    private DeviceRepo deviceRepo;

    @Autowired
    private UserRepo userRepo;

    // ============================
    // GET ALL
    // ============================

    public List<DeviceResponseDto> getAllDevices() {
        return deviceRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DeviceResponseDto> getDevicesByUserDni(Integer userDni) {
        User user = userRepo.findByDni(Objects.requireNonNull(userDni, "User DNI vacío"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        return deviceRepo.findByUser(user).stream()
                .map(this::toResponse)
                .toList();
    }

    public DeviceResponseDto getDeviceBySerialNumber(String serialNumber) {
        Device device = deviceRepo.findBySerialNumber(serialNumber);
        return device != null ? toResponse(device) : null;
    }

    public List<DeviceResponseDto> getDevicesByBrand(String brand) {
        return deviceRepo.findByBrand(brand).stream()
                .map(this::toResponse)
                .toList();
    }

    // ============================
    // GET BY ID
    // ============================

    public DeviceResponseDto getDeviceById(UUID id) {

        Device device = deviceRepo.findById(
                Objects.requireNonNull(id, "ID vacío"))
                .orElseThrow(() -> new RuntimeException("Device not found"));

        return toResponse(device);
    }

    public Device getDeviceEntityById(UUID id) {
        return deviceRepo.findById(Objects.requireNonNull(id, "ID vacío"))
                .orElseThrow(() -> new RuntimeException("Device not found"));
    }

    // ============================
    // SAVE
    // ============================

    public DeviceResponseDto saveDevice(DeviceCreateRequestDto request) {

        User user = userRepo.findById(
                Objects.requireNonNull(request.getUserId(), "User ID vacío"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        Device device = Device.builder()
                .type(request.getType())
                .brand(request.getBrand())
                .model(request.getModel())
                .serialNumber(request.getSerialNumber())
                .user(user)
                // snapshot
                .userName(user.getName())
                .userDni(user.getDni())
                .build();

        Device saved = deviceRepo.save(Objects.requireNonNull(device));

        return toResponse(saved);
    }

    // ============================
    // UPDATE
    // ============================

    public DeviceResponseDto updateDevice(DeviceUpdateRequestDto request) {

        Device existing = deviceRepo.findById(
                Objects.requireNonNull(request.getId(), "ID vacío"))
                .orElseThrow(() -> new RuntimeException("Device not found"));

        existing.setType(request.getType());
        existing.setBrand(request.getBrand());
        existing.setModel(request.getModel());
        existing.setSerialNumber(request.getSerialNumber());

        UUID userId = request.getUserId();
        if (userId != null) {

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            existing.setUser(user);

            // actualizar snapshot
            existing.setUserName(user.getName());
            existing.setUserDni(user.getDni());
        }

        Device updated = deviceRepo.save(existing);

        return toResponse(updated);
    }

    // ============================
    // DELETE
    // ============================

    public void deleteDevice(UUID id) {

        if (!deviceRepo.existsById(
                Objects.requireNonNull(id, "ID vacío"))) {

            throw new RuntimeException("Device not found");
        }

        deviceRepo.deleteById(id);
    }

    // ============================
    // MAPPER
    // ============================

    private DeviceResponseDto toResponse(Device device) {

        return DeviceResponseDto.builder()
                .id(device.getId())
                .type(device.getType())
                .brand(device.getBrand())
                .model(device.getModel())
                .serialNumber(device.getSerialNumber())
                .userId(device.getUser() != null ? device.getUser().getId() : null)
                .userName(device.getUserName())
                .userDni(device.getUserDni())
                .build();
    }
}
