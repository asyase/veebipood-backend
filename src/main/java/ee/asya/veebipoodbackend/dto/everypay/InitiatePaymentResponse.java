package ee.asya.veebipoodbackend.dto.everypay;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InitiatePaymentResponse(
        @JsonProperty("payment_reference") String paymentReference,
        @JsonProperty("payment_link") String paymentLink,
        @JsonProperty("payment_state") String paymentState,
        @JsonProperty("order_reference") String orderReference,
        String currency,
        @JsonProperty("initial_amount") BigDecimal initialAmount) {
}
