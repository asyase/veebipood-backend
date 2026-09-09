package ee.asya.veebipoodbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.asya.veebipoodbackend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}