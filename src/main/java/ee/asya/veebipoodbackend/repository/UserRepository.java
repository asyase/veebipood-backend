package ee.asya.veebipoodbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.asya.veebipoodbackend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}