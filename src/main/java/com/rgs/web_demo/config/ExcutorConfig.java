package com.rgs.web_demo.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ExcutorConfig {
	
	    public Executor taskExecutor() {
	        return Executors.newFixedThreadPool(5);
	    }
}
