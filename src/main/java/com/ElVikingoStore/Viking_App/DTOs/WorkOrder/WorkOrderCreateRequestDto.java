package com.ElVikingoStore.Viking_App.DTOs.WorkOrder;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "WorkOrderCreateRequest", description = "Request para crear una orden de trabajo")
public class WorkOrderCreateRequestDto {

    @Schema(description = "Identificador único del cliente", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID clientId;

    @Schema(description = "Identificador único del dispositivo", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID deviceId;

    @Schema(description = "Descripción del problema", example = "Pantalla rota", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 200)
    private String issueDescription;

    @Schema(description = "Estado inicial de la reparación", example = "RECEIVED", allowableValues = {
            "RECEIVED",
            "IN_PROGRESS",
            "IN_QUEUE",
            "DONE",
            "WITHDRAWN"
    }, requiredMode = Schema.RequiredMode.REQUIRED)
    private String repairStatus;
}
