package com.balloon.controller;

import com.balloon.service.HealthCheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adam on 2017.03.12..
 */
@RestController
@RequestMapping("hc")
public class HealthCheckController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, HashMap<String, Boolean>>> getHealthChecks() {
        return new ResponseEntity<>(HealthCheckService.getPreviousHealthChecks(), HttpStatus.OK);
    }
}
