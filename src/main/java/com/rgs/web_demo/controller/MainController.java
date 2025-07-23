package com.rgs.web_demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @PostMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
