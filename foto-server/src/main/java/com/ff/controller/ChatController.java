package com.ff.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*") // 加这一行
public class ChatController {

    // 你启动的 Python 接口地址
    private static final String PYTHON_AGENT_URL = "http://localhost:8000/chat";

    @GetMapping("/send")
    public Map<String, Object> chat(@RequestParam("query") String query) {
        RestTemplate restTemplate = new RestTemplate();

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("query", query);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestMap, headers);

        // 发送 POST 请求给 Python
        Map<String, Object> response = restTemplate.postForObject(
                PYTHON_AGENT_URL,
                request,
                Map.class
        );

        return response;
    }
}