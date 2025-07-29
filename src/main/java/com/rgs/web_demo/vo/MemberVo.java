package com.rgs.web_demo.vo;

import com.rgs.web_demo.enumeration.MemberType;
import lombok.Data;

@Data
public class MemberVo {
    private String name;
    private String password;
    private String phoneNumber;
    private String email;
    private MemberType memberType;
    private String createdAt;
    private String updatedAt;
    private String memberUuid;
}
