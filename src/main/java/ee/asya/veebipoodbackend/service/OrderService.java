package ee.asya.veebipoodbackend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ee.asya.veebipoodbackend.dto.CreateOrderRequest;
import ee.asya.veebipoodbackend.dto.OrderItemRequest;
import ee.asya.veebipoodbackend.dto.everypay.InitiatePaymentResponse;
import ee.asya.veebipoodbackend.entity.Order;
import ee.asya.veebipoodbackend.entity.OrderItem;
import ee.asya.veebipoodbackend.entity.OrderStatus;
import ee.asya.veebipoodbackend.entity.Product;
import ee.asya.veebipoodbackend.entity.User;
import ee.asya.veebipoodbackend.email.EmailService;
import ee.asya.veebipoodbackend.everypay.EveryPayService;
import ee.asya.veebipoodbackend.repository.OrderRepository;
import ee.asya.veebipoodbackend.repository.ProductRepository;
import ee.asya.veebipoodbackend.repository.UserRepository;
import ee.asya.veebipoodbackend.smartpost.SmartPostService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SmartPostService smartPostService;
    private final EveryPayService everyPayService;
    private final EmailService emailService;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
        }
        if (isBlank(request.placeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "placeId is required");
        }
        if (isBlank(request.recipientName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recipientName is required");
        }
        if (isBlank(request.recipientPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recipientPhone is required");
        }

        User currentUser = getCurrentUser();

        Order order = new Order();
        order.setUser(currentUser);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>());
        order.setPlaceId(request.placeId());
        order.setRecipientName(request.recipientName());
        order.setRecipientPhone(request.recipientPhone());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.items()) {
            OrderItem orderItem = buildOrderItem(order, itemRequest);
            order.getOrderItems().add(orderItem);
            total = total.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);
        emailService.sendOrderConfirmation(currentUser.getEmail(), saved);
        return saved;
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> updateStatus(Long id, OrderStatus status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            return orderRepository.save(order);
        });
    }

    public boolean delete(Long id) {
        if (!orderRepository.existsById(id)) {
            return false;
        }
        orderRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Optional<Order> generateBarcode(Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            if (isBlank(order.getPlaceId()) || isBlank(order.getRecipientName()) || isBlank(order.getRecipientPhone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Order is missing shipping details (placeId, recipientName, recipientPhone)");
            }
            String barcode = smartPostService.createOrder(
                    order.getPlaceId(), order.getRecipientName(), order.getRecipientPhone());
            order.setBarcode(barcode);
            return orderRepository.save(order);
        });
    }

    @Transactional
    public Optional<Order> initiatePayment(Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            InitiatePaymentResponse response = everyPayService.initiateOneOffPayment(
                    order.getTotalPrice(), String.valueOf(order.getId()));
            order.setPaymentReference(response.paymentReference());
            order.setPaymentLink(response.paymentLink());
            order.setPaymentState(response.paymentState());
            return orderRepository.save(order);
        });
    }

    private OrderItem buildOrderItem(Order order, OrderItemRequest itemRequest) {
        if (itemRequest.quantity() == null || itemRequest.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }

        Product product = productRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found: " + itemRequest.productId()));
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not available: " + product.getName());
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(itemRequest.quantity());
        orderItem.setPrice(product.getPrice());
        return orderItem;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
