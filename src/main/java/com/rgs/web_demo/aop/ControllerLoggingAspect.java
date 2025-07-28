package com.rgs.web_demo.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ControllerLoggingAspect {

    private final ObjectMapper objectMapper;

    @Around("execution(* com.rgs.web_demo.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = getHttpServletRequest();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Request 로깅
        logRequest(request, className, methodName, joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            // Response 로깅
            logResponse(className, methodName, result, endTime - startTime);

            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();

            // Exception 로깅
            logException(className, methodName, e, endTime - startTime);

            throw e;
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private void logRequest(HttpServletRequest request, String className, String methodName, Object[] args) {
        if (request == null) return;

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== HTTP REQUEST ==========\n");
        logMessage.append("Controller: ").append(className).append(".").append(methodName).append("\n");
        logMessage.append("HTTP Method: ").append(request.getMethod()).append("\n");
        logMessage.append("Request URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Query String: ").append(request.getQueryString()).append("\n");
        logMessage.append("Remote Address: ").append(request.getRemoteAddr()).append("\n");

        // Headers
        logMessage.append("Headers: \n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logMessage.append("  ").append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        }

        // Parameters
        if (args != null && args.length > 0) {
            logMessage.append("Request Body/Parameters: \n");
            for (int i = 0; i < args.length; i++) {
                try {
                    String argJson = objectMapper.writeValueAsString(args[i]);
                    logMessage.append("  Arg[").append(i).append("]: ").append(argJson).append("\n");
                } catch (Exception e) {
                    logMessage.append("  Arg[").append(i).append("]: ").append(args[i]).append("\n");
                }
            }
        }

        logMessage.append("==================================");
        log.info(logMessage.toString());
    }

    private void logResponse(String className, String methodName, Object result, long executionTime) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== HTTP RESPONSE ==========\n");
        logMessage.append("Controller: ").append(className).append(".").append(methodName).append("\n");
        logMessage.append("Execution Time: ").append(executionTime).append("ms\n");

        if (result != null) {
            try {
                String resultJson = objectMapper.writeValueAsString(result);
                logMessage.append("Response Body: \n").append(resultJson).append("\n");
            } catch (Exception e) {
                logMessage.append("Response: ").append(result).append("\n");
            }
        } else {
            logMessage.append("Response: null\n");
        }

        logMessage.append("===================================");
        log.info(logMessage.toString());
    }

    private void logException(String className, String methodName, Exception exception, long executionTime) {
        String logMessage = "\n========== HTTP EXCEPTION ==========\n" +
                "Controller: " + className + "." + methodName + "\n" +
                "Execution Time: " + executionTime + "ms\n" +
                "Exception Type: " + exception.getClass().getSimpleName() + "\n" +
                "Exception Message: " + exception.getMessage() + "\n" +
                "====================================";
        log.error(logMessage, exception);
    }
}