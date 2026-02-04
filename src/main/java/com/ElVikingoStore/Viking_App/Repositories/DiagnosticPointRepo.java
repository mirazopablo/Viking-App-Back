package com.ElVikingoStore.Viking_App.Repositories;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ElVikingoStore.Viking_App.Models.DiagnosticPoint;

@Repository
public interface DiagnosticPointRepo extends JpaRepository<DiagnosticPoint, UUID> {
    @Operation(summary = "Buscar puntos de diagnóstico por ID de orden de trabajo")
    List<DiagnosticPoint> findByWorkOrder_Id(UUID workOrderId);
}
