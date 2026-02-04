package com.ElVikingoStore.Viking_App.Resources;

import com.ElVikingoStore.Viking_App.Services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ElVikingoStore.Viking_App.DTOs.WorkOrder.WorkOrderCreateRequestDto;
import com.ElVikingoStore.Viking_App.DTOs.WorkOrder.WorkOrderResponseDto;
import com.ElVikingoStore.Viking_App.DTOs.WorkOrder.WorkOrderStatusUpdateRequestDto;
import com.ElVikingoStore.Viking_App.Services.WorkOrderService;

import java.util.UUID;

@Tag(name = "Work Order Controller", description = "API para la gestión de órdenes de trabajo")
@RestController
@RequestMapping("/api/work-order")
public class WorkOrderResource {

    @Value("${admin.role-id}")
    private String adminRoleId;

    private final WorkOrderService workOrderService;
    private final UserService userService;

    public WorkOrderResource(WorkOrderService workOrderService,
            UserService userService) {
        this.workOrderService = workOrderService;
        this.userService = userService;
    }

    // =============================
    // SEARCH
    // =============================

    @GetMapping("/search")
    public ResponseEntity<?> searchWorkOrder(
            @RequestParam(required = false) UUID staffId,
            @RequestParam(required = false) Integer clientDni,
            @RequestParam(required = false) String deviceSerialNumber,
            @RequestParam(required = false) String query) {

        if (query == null) {
            return ResponseEntity.badRequest().body("Query parameter is required");
        }

        return switch (query.toLowerCase()) {

            case "all" ->
                ResponseEntity.ok(workOrderService.getAllWorkOrders());

            case "by-staffid" -> {
                if (staffId == null)
                    yield ResponseEntity.badRequest().body("Staff ID required");
                yield ResponseEntity.ok(workOrderService.getWorkOrdersByStaffId(staffId));
            }

            case "by-clientdni" -> {
                if (clientDni == null)
                    yield ResponseEntity.badRequest().body("Client DNI required");
                yield ResponseEntity.ok(workOrderService.getWorkOrdersByClientDni(clientDni));
            }

            case "by-deviceserialnumber" -> {
                if (deviceSerialNumber == null)
                    yield ResponseEntity.badRequest().body("Device serial number required");
                yield ResponseEntity.ok(workOrderService.getWorkOrdersByDeviceSerialNumber(deviceSerialNumber));
            }

            default -> ResponseEntity.badRequest().body("Invalid query parameter");
        };
    }

    // =============================
    // CREATE
    // =============================

    @PostMapping("/save")
    public ResponseEntity<?> saveWorkOrder(
            @RequestBody WorkOrderCreateRequestDto request,
            Authentication authentication) {

        String email = authentication.getName();
        UUID staffRoleId = UUID.fromString(adminRoleId);

        if (!userService.hasRoleId(email, staffRoleId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: User is not staff");
        }

        WorkOrderResponseDto response = workOrderService.createWorkOrder(request, email);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/{orderId}")
    public ResponseEntity<?> updateWorkOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody WorkOrderStatusUpdateRequestDto request) {

        workOrderService.updateWorkOrderStatus(orderId, request.getRepairStatus());

        return ResponseEntity.ok("Work order status updated successfully");
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteWorkOrder(@PathVariable UUID orderId) {
        workOrderService.deleteWorkOrder(orderId);
        return ResponseEntity.ok("Work order deleted successfully");
    }
}
