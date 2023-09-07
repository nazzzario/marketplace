package com.teamchallenge.marketplace.controller;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<Object> hello() {
        Message message = new Message();
        message.key = "message";
        message.value = "hello from Marketplace project!";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @Getter
    static class Message {
        private String key;
        private String value;

    }
}
