package com.ElVikingoStore.Viking_App.DTOs.Device;

import java.util.UUID;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCreateRequestDto {

    private String type;
    private String brand;
    private String model;
    private String serialNumber;
    private UUID userId;

}
