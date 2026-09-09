package ee.asya.veebipoodbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.asya.veebipoodbackend.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
