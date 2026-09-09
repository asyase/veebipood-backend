package ee.asya.veebipoodbackend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ee.asya.veebipoodbackend.repository.OrderItemRepository;
import ee.asya.veebipoodbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public boolean delete(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        if (orderItemRepository.existsByProductId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Product cannot be deleted because it is already used in an order.");
        }
        productRepository.deleteById(id);
        return true;
    }
}
