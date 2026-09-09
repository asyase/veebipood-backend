package ee.asya.veebipoodbackend.smartpost;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import ee.asya.veebipoodbackend.dto.smartpost.ParcelTerminal;
import ee.asya.veebipoodbackend.dto.smartpost.SmartPostOrderRequest;
import ee.asya.veebipoodbackend.dto.smartpost.SmartPostOrderResponse;
import ee.asya.veebipoodbackend.dto.smartpost.SmartPostPlacesResponse;

@Service
public class SmartPostService {

    private final RestClient restClient;

    public SmartPostService(
            @Value("${smartpost.api.base-url}") String baseUrl,
            @Value("${smartpost.api.key}") String apiKey,
            @Value("${smartpost.api.gateway-secret}") String gatewaySecret) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", apiKey)
                .defaultHeader("X-GATEWAY-SECRET", gatewaySecret)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public List<ParcelTerminal> getPlaces(String country, String type, String filter) {
        SmartPostPlacesResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("places")
                        .queryParamIfPresent("country", Optional.ofNullable(country))
                        .queryParamIfPresent("type", Optional.ofNullable(type))
                        .queryParamIfPresent("filter", Optional.ofNullable(filter))
                        .build())
                .retrieve()
                .body(SmartPostPlacesResponse.class);

        if (response == null || response.places() == null || response.places().item() == null) {
            return List.of();
        }
        return response.places().item();
    }

    public String createOrder(String placeId, String recipientName, String recipientPhone) {
        SmartPostOrderRequest request = new SmartPostOrderRequest(
                new SmartPostOrderRequest.Orders(
                        new SmartPostOrderRequest.Item(
                                new SmartPostOrderRequest.Source("EE"),
                                new SmartPostOrderRequest.Recipient(recipientName, recipientPhone),
                                new SmartPostOrderRequest.Destination(placeId))));

        SmartPostOrderResponse response = restClient.post()
                .uri("orders")
                .body(request)
                .retrieve()
                .body(SmartPostOrderResponse.class);

        if (response == null || response.orders() == null || response.orders().item() == null
                || response.orders().item().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "SmartPost did not return a barcode");
        }

        return response.orders().item().get(0).barcode();
    }

    public byte[] getLabel(String barcode, String format) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("labels")
                        .queryParam("barcode", barcode)
                        .queryParam("format", format)
                        .build())
                .retrieve()
                .body(byte[].class);
    }
}
