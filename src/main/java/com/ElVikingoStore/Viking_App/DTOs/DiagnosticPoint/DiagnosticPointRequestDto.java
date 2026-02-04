package com.ElVikingoStore.Viking_App.DTOs.DiagnosticPoint;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Builder
public class DiagnosticPointRequestDto {
    private UUID workOrderId;
    private String description;
    private String notes;
    private LocalDateTime timestamp; // Opcional, si se envía desde el cliente
}
