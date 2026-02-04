package com.ElVikingoStore.Viking_App.DTOs.WorkOrder;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "WorkOrderResponse", description = "Respuesta de una orden de trabajo")
public class WorkOrderResponseDto {

    private UUID id;

    private UUID clientId;
    private String clientName;
    private Integer clientDni;

    private UUID staffId;

    private UUID deviceId;
    private String deviceBrand;
    private String deviceModel;
    private String deviceSerialNumber;

    private String issueDescription;
    private String repairStatus;

    private LocalDateTime createdAt;
}
