package ee.asya.veebipoodbackend.dto;

import ee.asya.veebipoodbackend.entity.OrderStatus;

public record UpdateOrderStatusRequest(OrderStatus status) {
}
