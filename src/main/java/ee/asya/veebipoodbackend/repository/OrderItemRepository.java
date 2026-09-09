package ee.asya.veebipoodbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.asya.veebipoodbackend.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByProductId(Long productId);
}
