package com.ElVikingoStore.Viking_App.DTOs.DiagnosticPoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticPointResponseDto {
    private UUID id;
    private UUID workOrderId;
    private LocalDateTime timestamp;
    private String description;
    private String notes;
    private List<String> multimediaFiles;
}