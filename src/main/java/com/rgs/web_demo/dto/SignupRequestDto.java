package com.rgs.web_demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequestDto {
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    private String phoneNumber;

    @NotBlank
    private String memberType;
}
