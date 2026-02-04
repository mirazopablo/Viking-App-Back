package com.ElVikingoStore.Viking_App.Services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.ElVikingoStore.Viking_App.DTOs.WorkOrder.WorkOrderCreateRequestDto;
import com.ElVikingoStore.Viking_App.DTOs.WorkOrder.WorkOrderResponseDto;

import com.ElVikingoStore.Viking_App.Models.Device;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ElVikingoStore.Viking_App.Models.User;
import com.ElVikingoStore.Viking_App.Models.WorkOrder;
import com.ElVikingoStore.Viking_App.Repositories.WorkOrderRepo;

import jakarta.persistence.EntityNotFoundException;

@Schema(name = "WorkOrderService", description = "Service for managing work orders")
@Service
public class WorkOrderService {

    private final WorkOrderRepo workOrderRepo;
    private final UserService userService;
    private final DeviceService deviceService;

    public WorkOrderService(WorkOrderRepo workOrderRepo,
            UserService userService,
            DeviceService deviceService) {
        this.workOrderRepo = workOrderRepo;
        this.userService = userService;
        this.deviceService = deviceService;
    }

    // =============================
    // CREATE
    // =============================

    @Transactional
    public WorkOrderResponseDto createWorkOrder(WorkOrderCreateRequestDto request, String staffEmail) {

        User staff = findUserByEmail(staffEmail);
        User client = findUser(request.getClientId(), "Client");
        Device device = findAndValidateDevice(request.getDeviceId(), client);

        WorkOrder workOrder = WorkOrder.builder()
                .client(client)
                .staff(staff)
                .device(device)
                .issueDescription(request.getIssueDescription())
                .repairStatus(request.getRepairStatus())
                // snapshot
                .clientName(client.getName())
                .clientDni(client.getDni())
                .deviceBrand(device.getBrand())
                .deviceModel(device.getModel())
                .deviceSerialNumber(device.getSerialNumber())
                .build();

        WorkOrder saved = workOrderRepo.save(Objects.requireNonNull(workOrder));

        return convertToResponse(saved);
    }

    // =============================
    // READ
    // =============================

    public List<WorkOrderResponseDto> getAllWorkOrders() {
        return workOrderRepo.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public WorkOrderResponseDto getWorkOrderById(UUID id) {
        return convertToResponse(getWorkOrderEntityById(id));
    }

    public WorkOrder getWorkOrderEntityById(UUID id) {
        return workOrderRepo.findById(Objects.requireNonNull(id, "ID vacío"))
                .orElseThrow(() -> new EntityNotFoundException("Work order not found"));
    }

    public List<WorkOrderResponseDto> getWorkOrdersByStaffId(UUID staffId) {
        return workOrderRepo.findByStaffId(staffId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<WorkOrderResponseDto> getWorkOrdersByClientDni(Integer clientDni) {
        return workOrderRepo.findByClientDni(clientDni)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<WorkOrderResponseDto> getWorkOrdersByDeviceSerialNumber(String deviceSerialNumber) {
        return workOrderRepo.findByDeviceSerialNumber(deviceSerialNumber)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =============================
    // UPDATE STATUS
    // =============================

    @Transactional
    public void updateWorkOrderStatus(UUID orderId, String newStatus) {
        WorkOrder workOrder = workOrderRepo.findById(Objects.requireNonNull(orderId, "ID vacío"))
                .orElseThrow(() -> new EntityNotFoundException("Work order not found"));

        workOrder.setRepairStatus(newStatus);
    }

    // =============================
    // PRIVATE HELPERS
    // =============================

    private User findUserByEmail(String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    private User findUser(UUID userId, String userType) {
        return userService.getUserEntityById(userId);
    }

    private Device findAndValidateDevice(UUID deviceId, User client) {
        Device device = deviceService.getDeviceEntityById(deviceId);

        if (!device.getUser().getId().equals(client.getId())) {
            throw new IllegalArgumentException("Device does not belong to the specified client");
        }

        return device;
    }

    @Operation
    public boolean deleteWorkOrder(UUID workOrderId) {
        try {
            Optional<WorkOrder> workOrder = workOrderRepo.findById(Objects.requireNonNull(workOrderId, "ID vacío"));
            if (workOrder.isEmpty()) {
                return false;
            }
            workOrderRepo.deleteById(workOrderId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private WorkOrderResponseDto convertToResponse(WorkOrder workOrder) {
        return WorkOrderResponseDto.builder()
                .id(workOrder.getId())
                .clientId(workOrder.getClient().getId())
                .staffId(workOrder.getStaff().getId())
                .deviceId(workOrder.getDevice().getId())
                .issueDescription(workOrder.getIssueDescription())
                .repairStatus(workOrder.getRepairStatus())
                .clientName(workOrder.getClientName())
                .clientDni(workOrder.getClientDni())
                .deviceBrand(workOrder.getDeviceBrand())
                .deviceModel(workOrder.getDeviceModel())
                .deviceSerialNumber(workOrder.getDeviceSerialNumber())
                .createdAt(workOrder.getCreatedAt())
                .build();
    }
}
