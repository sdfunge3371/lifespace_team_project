package com.lifespace.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestJsonController {

    public static class Dummy {
        public String name;
    }

    @PostMapping(value = "/json", consumes = "application/json")
    public ResponseEntity<String> testJson(@RequestBody Dummy dummy) {
        return ResponseEntity.ok("收到 name: " + dummy.name);
    }
}
