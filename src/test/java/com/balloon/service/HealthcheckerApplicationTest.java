package com.balloon.service;

import com.balloon.model.HealthCheckModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class HealthcheckerApplicationTest {

    public static final String REDIS = "redis";
    public static final String MYSQL = "mysql";

    @Value("${app.check.url.node-aruba-1}")
    private String urlNodeAruba1;

    @Value("${app.check.url.node-aruba-2}")
    private String urlNodeAruba2;

    @Value("${app.check.url.lobby}")
    private String urlLobby;

    @Value("${app.check.url.rancher}")
    private String urlRancher;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MailService mailService;

    @InjectMocks
    private HealthCheckService healthCheckService;

    @BeforeEach
    public void setupMocks() {
        var healthCheckModelNodeAruba1 = new HealthCheckModel();
        var checksForNodeAruba1 = Map.of(
                REDIS, true,
                MYSQL, true
        );
        healthCheckModelNodeAruba1.setResources(checksForNodeAruba1);
        healthCheckModelNodeAruba1.setStatus(true);

        when(restTemplate.getForObject(urlNodeAruba1, HealthCheckModel.class)).thenReturn(healthCheckModelNodeAruba1);

        HealthCheckModel healthCheckModelNodeAruba2 = new HealthCheckModel();
        var checksForNodeAruba2 = Map.of(
                REDIS, false,
                MYSQL, true
        );
        healthCheckModelNodeAruba2.setResources(checksForNodeAruba2);
        healthCheckModelNodeAruba2.setStatus(true);

        when(restTemplate.getForObject(urlNodeAruba2, HealthCheckModel.class)).thenReturn(healthCheckModelNodeAruba2);
    }

    @Test
    public void aCheckIfEverythingIsOK() {
        HealthCheckModel node1HealthCheck = healthCheckService.getHealthCheck(urlNodeAruba1);
        Map<String, Boolean> checks = node1HealthCheck.getResources();
        assertFalse(checks.containsValue(false));
        assertTrue(checks.get(REDIS));
        assertTrue(checks.get(MYSQL));
        assertEquals(node1HealthCheck.getServer(), urlNodeAruba1);

        verify(mailService, never()).sendMail(any());
    }

    @Test
    public void bCheckIfOneValueIsFalse() {
        HealthCheckModel node2HealthCheck = healthCheckService.getHealthCheck(urlNodeAruba2);
        Map<String, Boolean> checks = node2HealthCheck.getResources();
        assertTrue(checks.containsValue(false));
        assertFalse(checks.get(REDIS));
        assertTrue(checks.get(MYSQL));
        assertEquals(node2HealthCheck.getServer(), urlNodeAruba2);

        verify(mailService, times(1)).sendMail(any());
    }

    @Test
    public void dCheckIfNotNewErrorThanEmailNotSent() {
        HealthCheckModel node2HealthCheck = healthCheckService.getHealthCheck(urlNodeAruba2);
        Map<String, Boolean> checks = node2HealthCheck.getResources();
        assertTrue(checks.containsValue(false));
        assertFalse(checks.get(REDIS));
        assertTrue(checks.get(MYSQL));
        assertEquals(node2HealthCheck.getServer(), urlNodeAruba2);

        verify(mailService, never()).sendMail(any());
    }

    @Test
    public void eCheckIfFixedThanEmailSent() {
        var checksForNodeAruba2 = Map.of(
                REDIS, true,
                MYSQL, true
        );
        HealthCheckModel responseForNodeAruba2 = new HealthCheckModel();
        responseForNodeAruba2.setResources(checksForNodeAruba2);
        responseForNodeAruba2.setStatus(true);

        when(restTemplate.getForObject(urlNodeAruba2, HealthCheckModel.class)).thenReturn(responseForNodeAruba2);

        HealthCheckModel node2HealthCheck = healthCheckService.getHealthCheck(urlNodeAruba2);
        Map<String, Boolean> checks = node2HealthCheck.getResources();
        assertFalse(checks.containsValue(false));
        assertTrue(checks.get(REDIS));
        assertTrue(checks.get(MYSQL));
        assertEquals(node2HealthCheck.getServer(), urlNodeAruba2);

        verify(mailService, times(1)).sendMail(any());
    }

}
