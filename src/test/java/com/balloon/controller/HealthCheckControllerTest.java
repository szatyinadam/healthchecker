package com.balloon.controller;

import com.balloon.service.HealthCheckService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by aszatyin on 2017-03-14.
 */
@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckControllerTest {

    @Value("${local.server.port}")
    private int port;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Value("${app.check.url.node-aruba-1}")
    private String urlNodeAruba1;

    @Value("${app.check.url.node-aruba-2}")
    private String urlNodeAruba2;

    private String url;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void checkRestEndpoint() {
        var checks = Map.of("mysql", true, "redis", true);
        HealthCheckService.getPreviousHealthChecks().put(urlNodeAruba1, checks);
        HealthCheckService.getPreviousHealthChecks().put(urlNodeAruba2, checks);

        url = "http://localhost:" + port;
        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
        var responseType = new ParameterizedTypeReference<Map<String, Map<String, Boolean>>>() {
        };

        ResponseEntity<Map<String, Map<String, Boolean>>> response = restTemplate.exchange(url + "/hc", HttpMethod.GET, null, responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().containsKey(urlNodeAruba1));
        assertTrue(response.getBody().containsKey(urlNodeAruba2));
        assertTrue(response.getBody().get(urlNodeAruba1).get("mysql"));
        assertTrue(response.getBody().get(urlNodeAruba1).get("redis"));
        assertTrue(response.getBody().get(urlNodeAruba2).get("mysql"));
        assertTrue(response.getBody().get(urlNodeAruba2).get("redis"));
    }
}
