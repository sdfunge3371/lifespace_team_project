package com.lifespace.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ApiKeyController {

    @Value("${GOOGLE_MAPS_API_KEY}")
    private String googleMapsApiKey;

    @GetMapping("/google-maps-key")
    public Map<String, String> getGoogleMapsApiKey() {
        return Collections.singletonMap("key", googleMapsApiKey);
    }
}
