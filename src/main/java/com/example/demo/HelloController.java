package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @Value("${GREETING_MSG:Hello from DevOps Demo Service!}")
    private String greetingMessage;

    @Value("${APP_ENV:development}")
    private String appEnv;

    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", greetingMessage);
        response.put("environment", appEnv);
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "DevOps Pipeline is operational!");
        return response;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "devops-demo");
        status.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(status);
    }
}
