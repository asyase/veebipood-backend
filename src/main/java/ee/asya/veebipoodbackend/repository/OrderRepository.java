package ee.asya.veebipoodbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.asya.veebipoodbackend.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}