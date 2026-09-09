package ee.asya.veebipoodbackend.everypay;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import ee.asya.veebipoodbackend.dto.everypay.InitiatePaymentRequest;
import ee.asya.veebipoodbackend.dto.everypay.InitiatePaymentResponse;

@Service
public class EveryPayService {

    private final RestClient restClient;
    private final String accountName;
    private final String apiUsername;
    private final String customerUrl;

    public EveryPayService(
            @Value("${everypay.api.base-url}") String baseUrl,
            @Value("${everypay.api.username}") String apiUsername,
            @Value("${everypay.api.password}") String apiSecret,
            @Value("${everypay.api.account-name}") String accountName,
            @Value("${everypay.customer-url}") String customerUrl) {
        this.accountName = accountName;
        this.apiUsername = apiUsername;
        this.customerUrl = customerUrl;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.setBasicAuth(apiUsername, apiSecret);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }

    public InitiatePaymentResponse initiateOneOffPayment(BigDecimal amount, String orderReference) {
        InitiatePaymentRequest request = new InitiatePaymentRequest(
                accountName,
                UUID.randomUUID().toString(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                amount,
                orderReference,
                customerUrl,
                apiUsername);

        return restClient.post()
                .uri("/v4/payments/oneoff")
                .body(request)
                .retrieve()
                .body(InitiatePaymentResponse.class);
    }
}
