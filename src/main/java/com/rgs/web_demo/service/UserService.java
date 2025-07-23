package com.rgs.web_demo.service;

import org.springframework.stereotype.Service;

import com.rgs.web_demo.serviceImpl.UserServiceImpl;

@Service("UserService")
public class UserService implements UserServiceImpl {

    public boolean authenticate(String username, String password) {
        // 테스트
        return "testuser".equals(username) && "testpass".equals(password);
    }

}
