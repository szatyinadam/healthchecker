package com.balloon.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;

/**
 * Created by adam on 2017.03.12..
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class HealthCheckModel {
    private String server;
    private Boolean status;
    private HashMap<String, Boolean> resources;

    public HealthCheckModel(String server) {
        this.server = server;
    }
}
