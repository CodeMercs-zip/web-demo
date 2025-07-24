package com.rgs.web_demo.service;

import org.springframework.stereotype.Service;

@Service("UserService")
public class UserService {

    public boolean authenticate(String username, String password) {
        return "testuser".equals(username) && "testpass".equals(password);
    }

}