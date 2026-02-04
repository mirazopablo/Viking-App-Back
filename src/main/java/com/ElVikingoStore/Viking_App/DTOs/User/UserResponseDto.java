package com.ElVikingoStore.Viking_App.DTOs.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserResponseDto {

    private UUID id;
    private String name;
    private Integer dni;
    private String address;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private String email;

    private UUID roleId;
}
