package com.ElVikingoStore.Viking_App.DTOs.Device;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceUpdateRequestDto {

    private UUID id;

    private String type;
    private String brand;
    private String model;
    private String serialNumber;

    private UUID userId;
}
