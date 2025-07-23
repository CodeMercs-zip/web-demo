package com.rgs.web_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, W orld!";
    }

    @GetMapping("/hello3")
    public String hello2() {
        return "Hello, World!";
    }
}
