package com.rgs.web_demo.vo;

import lombok.Data;

@Data
public class MemberVo {
    String name;
    String password;
    String phoneNumber;
    String email;
    String memberType;
    String createdAt;
    String updatedAt;
    String memberUuid;
}
