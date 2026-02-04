package com.ElVikingoStore.Viking_App.DTOs.User;

import java.util.UUID;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserUpdateRequestDto {

    private UUID id;
    private String name;
    private String address;
    private String phoneNumber;
    private String secondaryPhoneNumber;
    private String email;
    private Integer dni;
    private String password;
    private UUID roleId;
}
