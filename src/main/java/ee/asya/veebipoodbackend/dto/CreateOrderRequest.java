package ee.asya.veebipoodbackend.dto;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItemRequest> items,
        String placeId,
        String recipientName,
        String recipientPhone) {
}
