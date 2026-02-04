package com.ElVikingoStore.Viking_App.DTOs.WorkOrder;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderStatusUpdateRequestDto {

    @NotBlank
    private String repairStatus;
}
