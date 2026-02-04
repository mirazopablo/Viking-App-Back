package com.ElVikingoStore.Viking_App.DTOs.Device;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponseDto {

    private UUID id;
    private String type;
    private String brand;
    private String model;
    private String serialNumber;
    private UUID userId;
    private String userName;
    private Integer userDni;

}
