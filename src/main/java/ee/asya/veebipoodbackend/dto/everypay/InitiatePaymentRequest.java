package ee.asya.veebipoodbackend.dto.everypay;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InitiatePaymentRequest(
        @JsonProperty("account_name") String accountName,
        String nonce,
        String timestamp,
        BigDecimal amount,
        @JsonProperty("order_reference") String orderReference,
        @JsonProperty("customer_url") String customerUrl,
        @JsonProperty("api_username") String apiUsername) {
}
