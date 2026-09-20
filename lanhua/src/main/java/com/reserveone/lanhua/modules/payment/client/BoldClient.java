package com.reserveone.lanhua.modules.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class BoldClient {

    private static final String BASE_URL = "https://integrations.api.bold.co";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${bold.identity-key}")
    private String identityKey;

    @Value("${bold.callback-url:}")
    private String callbackUrl;

    @SuppressWarnings("unchecked")
    public BoldLink createLink(String reference, long amount, String description) {
        Map<String, Object> body = new HashMap<>();
        body.put("amount_type", "CLOSE");
        body.put("amount", Map.of(
                "currency", "COP",
                "total_amount", amount,
                "tip_amount", 0));
        body.put("reference", reference);
        body.put("description", description);
        if (callbackUrl != null && !callbackUrl.isBlank()) {
            body.put("callback_url", callbackUrl);
        }

        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + "/online/link/v1",
                HttpMethod.POST,
                new HttpEntity<>(body, headers()),
                Map.class);

        Map<String, Object> payload = (Map<String, Object>) response.getBody().get("payload");
        return new BoldLink((String) payload.get("payment_link"), (String) payload.get("url"));
    }

    @SuppressWarnings("unchecked")
    public String getLinkStatus(String linkId) {
        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + "/online/link/v1/" + linkId,
                HttpMethod.GET,
                new HttpEntity<>(headers()),
                Map.class);

        return (String) response.getBody().get("status");
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "x-api-key " + identityKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}