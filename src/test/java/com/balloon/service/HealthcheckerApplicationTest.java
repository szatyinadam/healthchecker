package com.balloon.service;

import com.balloon.model.HealthCheckModel;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class HealthcheckerApplicationTest {

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

    @Before
    public void setupMocks() {
        HealthCheckModel healthCheckModelNodeAruba1 = new HealthCheckModel();
        HashMap<String, Boolean> checksForNodeAruba1 = new HashMap<>();
        checksForNodeAruba1.put("redis", true);
        checksForNodeAruba1.put("mysql", true);
        healthCheckModelNodeAruba1.setResources(checksForNodeAruba1);
        healthCheckModelNodeAruba1.setStatus(true);

        when(restTemplate.getForObject(urlNodeAruba1, HealthCheckModel.class)).thenReturn(healthCheckModelNodeAruba1);

        HealthCheckModel healthCheckModelNodeAruba2 = new HealthCheckModel();
        HashMap<String, Boolean> checksForNodeAruba2 = new HashMap<>();
        checksForNodeAruba2.put("redis", false);
        checksForNodeAruba2.put("mysql", true);
        healthCheckModelNodeAruba2.setResources(checksForNodeAruba2);
        healthCheckModelNodeAruba2.setStatus(true);

        when(restTemplate.getForObject(urlNodeAruba2, HealthCheckModel.class)).thenReturn(healthCheckModelNodeAruba2);
    }

    @Test
    public void aCheckIfEverythingIsOK() {
        HealthCheckModel node1HealthCheck = healthCheckService.getHealthCheck(urlNodeAruba1);
        HashMap<String, Boolean> checks = node1HealthCheck.getResources();
        assertFalse(checks.containsValue(false));
        assertTrue(checks.get("redis"));
        assertTrue(checks.get("mysql"));
        assertEquals(node1HealthCheck.getServer(), urlNodeAruba1);

        verify(mailService, never()).sendMail(any());
    }

    @Test
    public void bCheckIfOneValueIsFalse() {
        HealthCheckModel node2HealthCheck = healthCheckService.getHealthCheck(urlNodeAruba2);
        HashMap<String, Boolean> checks = node2HealthCheck.getResources();
        assertTrue(checks.containsValue(false));
        assertFalse(checks.get("redis"));
        assertTrue(checks.get("mysql"));
        assertEquals(node2HealthCheck.getServer(), urlNodeAruba2);

        verify(mailService, times(1)).sendMail(any());
    }

    @Test
    public void dCheckIfNotNewErrorThanEmailNotSent() {
        HealthCheckModel node2HealthCheck = healthCheckService.getHealthCheck(urlNodeAruba2);
        HashMap<String, Boolean> checks = node2HealthCheck.getResources();
        assertTrue(checks.containsValue(false));
        assertFalse(checks.get("redis"));
        assertTrue(checks.get("mysql"));
        assertEquals(node2HealthCheck.getServer(), urlNodeAruba2);

        verify(mailService, never()).sendMail(any());
    }

    @Test
    public void eCheckIfFixedThanEmailSent() {
        HashMap<String, Boolean> checksForNodeAruba2 = new HashMap<>();
        checksForNodeAruba2.put("redis", true);
        checksForNodeAruba2.put("mysql", true);
        HealthCheckModel responseForNodeAruba2 = new HealthCheckModel();
        responseForNodeAruba2.setResources(checksForNodeAruba2);
        responseForNodeAruba2.setStatus(true);

        when(restTemplate.getForObject(urlNodeAruba2, HealthCheckModel.class)).thenReturn(responseForNodeAruba2);

        HealthCheckModel node2HealthCheck = healthCheckService.getHealthCheck(urlNodeAruba2);
        HashMap<String, Boolean> checks = node2HealthCheck.getResources();
        assertFalse(checks.containsValue(false));
        assertTrue(checks.get("redis"));
        assertTrue(checks.get("mysql"));
        assertEquals(node2HealthCheck.getServer(), urlNodeAruba2);

        verify(mailService, times(1)).sendMail(any());
    }

}
