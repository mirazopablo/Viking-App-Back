package com.ElVikingoStore.Viking_App.DTOs.User;

import java.util.UUID;

import lombok.Data;

@Data
public class UserCreateRequestDto {

    private String name;
    private Integer dni;
    private String address;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private String email;
    private String password;

    private UUID roleId;
}
