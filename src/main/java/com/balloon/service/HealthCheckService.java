package com.balloon.service;

import com.balloon.model.HealthCheckModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by adam on 2017.03.12..
 */
@Service
@Slf4j
@EnableScheduling
public class HealthCheckService {

    private static final Map<String, Map<String, Boolean>> previousHealthChecks = new HashMap<>();

    @Value("${app.check.url.node-aruba-1}")
    private String urlNodeAruba1;

    @Value("${app.check.url.node-aruba-2}")
    private String urlNodeAruba2;

    @Value("${app.check.url.username}")
    private String username;

    @Value("${app.check.url.password}")
    private String password;

    @Value("${app.check.url.lobby}")
    private String urlLobby;

    @Value("${app.check.url.rancher}")
    private String urlRancher;

    @Value("${app.check.url.connection-manager}")
    private String urlConnectionManager;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MailService mailService;

    public static Map<String, Map<String, Boolean>> getPreviousHealthChecks() {
        return previousHealthChecks;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void getAllHealthCheckAsync() {
        CompletableFuture.supplyAsync(() -> getHealthCheck(urlNodeAruba1));
        CompletableFuture.supplyAsync(() -> getHealthCheck(urlNodeAruba2));
        CompletableFuture.supplyAsync(() -> getHealthCheck(urlLobby));
        CompletableFuture.supplyAsync(() -> getHealthCheck(urlConnectionManager));
    }

    protected HealthCheckModel getHealthCheck(String healthCheckUrl) {
        HealthCheckModel healthCheckModel = new HealthCheckModel(healthCheckUrl);
        try {
            HealthCheckModel response = restTemplate.getForObject(healthCheckUrl, HealthCheckModel.class);
            healthCheckModel.setResources(response.getResources());
            healthCheckModel.setStatus(response.getStatus());
            checkForUnavailable(healthCheckModel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return healthCheckModel;
    }

    private void checkForUnavailable(HealthCheckModel healthCheckModel) {
        if (previousHealthChecks.get(healthCheckModel.getServer()) == null) {
            previousHealthChecks.put(healthCheckModel.getServer(), healthCheckModel.getResources());
            if (healthCheckModel.getResources().containsValue(false))
                sendAlert(healthCheckModel);
        }
        if (!healthCheckModel.getResources().equals(previousHealthChecks.get(healthCheckModel.getServer()))) {
            previousHealthChecks.put(healthCheckModel.getServer(), healthCheckModel.getResources());
            if (healthCheckModel.getResources().containsValue(false)) {
                sendAlert(healthCheckModel);
            } else {
                sendFixAlert(healthCheckModel);
            }
        } else {
            log.info(healthCheckModel.toString());
        }
    }

    private void sendFixAlert(HealthCheckModel healthCheckModel) {
        log.error("Fixed in healthcheck: " + healthCheckModel.toString());
        mailService.sendMail("Successfully fixed: " + healthCheckModel.toString());
    }

    private void sendAlert(HealthCheckModel healthCheckModel) {
        log.error("Error in healthcheck: " + healthCheckModel.toString());
        mailService.sendMail("Error occurs in: " + healthCheckModel.toString());
    }
}
