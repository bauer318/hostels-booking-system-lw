package ru.bmstu.kibamba.gateway.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.exception.ServiceUnavailableException;
import ru.bmstu.kibamba.gateway.faulttolerance.FTCircuitBreaker;
import ru.bmstu.kibamba.gateway.faulttolerance.FTCircuitBreakerState;
import ru.bmstu.kibamba.gateway.faulttolerance.FTCommandState;
import ru.bmstu.kibamba.gateway.faulttolerance.FTDelayedCommand;
import ru.bmstu.kibamba.gateway.payload.PaymentPut;
import ru.bmstu.kibamba.gateway.payload.PaymentRequest;
import ru.bmstu.kibamba.gateway.payload.PaymentResponse;
import ru.bmstu.kibamba.gateway.service.GatewayService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentServiceController {
    private static int maxCount = 2;
    private final RestTemplate restTemplate;
    private final GatewayService gatewayService;
    private final String baseUrl = "http://localhost:8060/api/v1/payments";

    private final FTCircuitBreaker paymentServiceCB;

    private Queue<FTDelayedCommand> delayedCommands;
    private final TaskScheduler scheduler;
    private int count = 0;
    private final Runnable healthCheck = new Runnable() {
        @Override
        public void run() {
            log.info("trying to re run ");
            try {
                log.info("manage availability");
                String healthUri = baseUrl.concat("/manage/health");
                restTemplate.getForEntity(healthUri, String.class);

            } catch (Exception e) {
                log.info("service not available");
                scheduler.schedule(this, new Date(System.currentTimeMillis() + 1L).toInstant());
                count = 0;
            }
        }
    };


    @Autowired
    public PaymentServiceController(RestTemplate restTemplate, GatewayService gatewayService, TaskScheduler scheduler) {
        this.restTemplate = restTemplate;
        this.gatewayService = gatewayService;
        this.scheduler = scheduler;
        this.paymentServiceCB = new FTCircuitBreaker();
        this.delayedCommands = new ArrayDeque<>();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public PaymentResponse createPayment(@RequestBody PaymentRequest paymentRequest) {
        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest);
        return restTemplate.postForObject(baseUrl, request, PaymentResponse.class);
    }

    @PutMapping(value = "/{paymentUid}", consumes = "application/json", produces = "application/json")
    public PaymentResponse updatePayment(@PathVariable("paymentUid") UUID paymentUid, @RequestBody PaymentPut paymentPut) {
        String uri = baseUrl.concat("/{paymentUid}");
        HttpHeaders headers = gatewayService.createHeader();
        HttpEntity<PaymentPut> entity = new HttpEntity<>(paymentPut, headers);
        return restTemplate.exchange(uri, HttpMethod.PUT, entity, PaymentResponse.class, paymentUid).getBody();
    }

    @GetMapping(value = "/{paymentUid}")
    public PaymentResponse getPayment(@PathVariable("paymentUid") UUID paymentUid) {
        try {
            if (count >= maxCount) {
                scheduler.schedule(healthCheck, new Date(System.currentTimeMillis() + 1L).toInstant());
                return fallbackTest();
            } else {
                String uri = baseUrl.concat("/{paymentUid}");
                return restTemplate.getForObject(uri, PaymentResponse.class, paymentUid);
            }
        }catch (Exception exception){
            count++;
            return null;
        }
    }

    private PaymentResponse fallbackTest() {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("");
        paymentResponse.setPrice(0);
        return paymentResponse;
    }

    private void checkServiceAvailability() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ResponseEntity<String> response;
                try {
                    String healthUri = baseUrl.concat("/manage/health");
                    response = restTemplate.getForEntity(healthUri, String.class);

                } catch (Exception e) {
                    paymentServiceCB.onFail();
                    delayedCommands.add(new FTDelayedCommand(FTCommandState.PAYMENT_UNCHECK, UUID.randomUUID(), null));
                    processDelayedCommands();
                    throw new ServiceUnavailableException("GATEWAY : payment service unavailable");
                }
                if (response.getStatusCode() == HttpStatus.OK) {
                    paymentServiceCB.onSuccess();
                } else {
                    paymentServiceCB.onFail();
                }
            }
        };
        this.paymentServiceCB.setRetryTimerTask(task);
    }

    private void processDelayedCommands() {
        if (delayedCommands.isEmpty()) {
            return;
        }
        while (!delayedCommands.isEmpty()) {
            FTDelayedCommand nextCommand = delayedCommands.element();
            if (Objects.requireNonNull(nextCommand.getType()) == FTCommandState.RESERVATION_UNCHECK) {
                if (!paymentServiceCB.getState().equals(FTCircuitBreakerState.CLOSED)) {
                    return;
                }
                try {
                    getPayment(nextCommand.getDataUid());
                } catch (Exception e) {
                    return;
                }
            }
            delayedCommands.remove();
        }
    }
}
