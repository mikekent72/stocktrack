package com.mikekent.stocktrack;

import com.mikekent.stocktrack.model.Category;
import com.mikekent.stocktrack.model.Product;
import com.mikekent.stocktrack.repository.ProductRepository;
import com.mikekent.stocktrack.service.ProductService;
import com.mikekent.stocktrack.exception.DuplicateSkuException;
import com.mikekent.stocktrack.exception.InsufficientStockException;
import com.mikekent.stocktrack.exception.InvalidProductException;
import com.mikekent.stocktrack.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductServiceTest {

    private ProductService productService;
    private FakeProductRepository productRepository;
    private Category category;

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        productService = new ProductService(productRepository);
        category = new Category(1, "Electronics");
    }

    @Test
    void shouldRejectNegativePrice() {
        Product product = new Product(
                "Keyboard",
                "KEY-001",
                category,
                new BigDecimal("-10.00"),
                10,
                5
        );

        assertThrows(
                InvalidProductException.class,
                () -> productService.createProduct(product)
        );
    }

    @Test
    void shouldRejectNegativeQuantity() {
        Product product = new Product(
                "Keyboard",
                "KEY-001",
                category,
                new BigDecimal("50.00"),
                -1,
                5
        );

        assertThrows(
                InvalidProductException.class,
                () -> productService.createProduct(product)
        );
    }

    @Test
    void shouldRejectEmptyProductName() {
        Product product = new Product(
                "",
                "KEY-001",
                category,
                new BigDecimal("50.00"),
                10,
                5
        );

        assertThrows(
                InvalidProductException.class,
                () -> productService.createProduct(product)
        );
    }

    @Test
    void shouldRejectZeroStockAmount() {
        Product product = new Product(
                1,
                "Keyboard",
                "KEY-001",
                category,
                new BigDecimal("50.00"),
                10,
                5
        );

        productRepository.save(product);

        assertThrows(
                InvalidProductException.class,
                () -> productService.addStock(1, 0)
        );
    }

    @Test
    void shouldRejectNegativeStockAmount() {
        Product product = new Product(
                1,
                "Keyboard",
                "KEY-001",
                category,
                new BigDecimal("50.00"),
                10,
                5
        );

        productRepository.save(product);

        assertThrows(
                InvalidProductException.class,
                () -> productService.removeStock(1, -2)
        );
    }

    @Test
    void shouldPreventRemovingMoreStockThanAvailable() {
        Product product = new Product(
                1,
                "Keyboard",
                "KEY-001",
                category,
                new BigDecimal("50.00"),
                5,
                2
        );

        productRepository.save(product);

        assertThrows(
                InsufficientStockException.class,
                () -> productService.removeStock(1, 6)
        );
    }

    @Test
    void shouldAddStock() {
        Product product = new Product(
                1,
                "Keyboard",
                "KEY-001",
                category,
                new BigDecimal("50.00"),
                5,
                2
        );

        productRepository.save(product);

        productService.addStock(1, 5);

        assertEquals(
                10,
                productRepository.findById(1).getQuantity()
        );
    }

    @Test
    void shouldRemoveStock() {
        Product product = new Product(
                1,
                "Keyboard",
                "KEY-001",
                category,
                new BigDecimal("50.00"),
                10,
                2
        );

        productRepository.save(product);

        productService.removeStock(1, 3);

        assertEquals(
                7,
                productRepository.findById(1).getQuantity()
        );
    }

    @Test
    void shouldDetectLowStock() {
        Product product = new Product(
                "Keyboard",
                "KEY-001",
                category,
                new BigDecimal("50.00"),
                3,
                5
        );

        assertTrue(productService.isLowStock(product));
    }

    @Test
    void shouldCalculateInventoryValue() {
        productRepository.save(
                new Product(
                        1,
                        "Keyboard",
                        "KEY-001",
                        category,
                        new BigDecimal("50.00"),
                        2,
                        5
                )
        );

        productRepository.save(
                new Product(
                        2,
                        "Mouse",
                        "MOU-001",
                        category,
                        new BigDecimal("25.00"),
                        4,
                        5
                )
        );

        assertEquals(
                new BigDecimal("200.00"),
                productService.calculateInventoryValue()
        );
    }

    @Test
    void shouldRejectDuplicateSku() {
        Product firstProduct = new Product(
                "Keyboard",
                "KEY-001",
                category,
                new BigDecimal("50.00"),
                10,
                5
        );

        productRepository.save(firstProduct);

        Product duplicateProduct = new Product(
                "Another Keyboard",
                "KEY-001",
                category,
                new BigDecimal("60.00"),
                5,
                2
        );

        assertThrows(
                DuplicateSkuException.class,
                () -> productService.createProduct(duplicateProduct)
        );
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(999)
        );
    }

    private static class FakeProductRepository
            extends ProductRepository {

        private final java.util.Map<Integer, Product> products =
                new java.util.HashMap<>();

        @Override
        public Product save(Product product) {
            if (product.getId() == 0) {
                product.setId(products.size() + 1);
            }

            products.put(product.getId(), product);
            return product;
        }

        @Override
        public Product findById(int id) {
            return products.get(id);
        }

        @Override
        public Product findBySku(String sku) {
            return products.values()
                    .stream()
                    .filter(product ->
                            product.getSku().equals(sku))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public java.util.List<Product> findAll() {
            return new java.util.ArrayList<>(products.values());
        }

        @Override
        public void update(Product product) {
            products.put(product.getId(), product);
        }
    }

}