package com.ElVikingoStore.Viking_App.Repositories;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ElVikingoStore.Viking_App.Models.WorkOrder;

@Repository
public interface WorkOrderRepo extends JpaRepository<WorkOrder, UUID> {
    @Operation(summary = "Buscar ordenes de trabajo por staffId")
    List<WorkOrder> findByStaffId(UUID staffId);

    @Operation(summary = "Buscar ordenes de trabajo por clientDni")
    List<WorkOrder> findByClientDni(Integer clientDni);

    @Operation(summary = "Buscar ordenes de trabajo por deviceSerialNumber")
    List<WorkOrder> findByDeviceSerialNumber(String deviceSerialNumber);
}
