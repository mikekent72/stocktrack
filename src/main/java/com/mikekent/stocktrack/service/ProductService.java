package com.mikekent.stocktrack.service;

import com.mikekent.stocktrack.model.Product;
import com.mikekent.stocktrack.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        validateProduct(product);

        if (productRepository.findBySku(product.getSku()) != null) {
            throw new IllegalArgumentException(
                    "A product with this SKU already exists."
            );
        }

        return productRepository.save(product);
    }

    public Product getProductById(int id) {
        Product product = productRepository.findById(id);

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product with ID " + id + " was not found."
            );
        }

        return product;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return getAllProducts();
        }

        return productRepository.search(searchTerm.trim());
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStock();
    }

    public void updateProduct(Product product) {
        validateProduct(product);

        Product existingProduct = getProductById(product.getId());

        Product productWithSameSku =
                productRepository.findBySku(product.getSku());

        if (productWithSameSku != null
                && productWithSameSku.getId() != existingProduct.getId()) {
            throw new IllegalArgumentException(
                    "A product with this SKU already exists."
            );
        }

        productRepository.update(product);
    }

    public void deleteProduct(int id) {
        getProductById(id);
        productRepository.deleteById(id);
    }

    public void addStock(int productId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Stock amount must be greater than zero."
            );
        }

        Product product = getProductById(productId);

        product.setQuantity(product.getQuantity() + amount);

        productRepository.update(product);
    }

    public void removeStock(int productId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Stock amount must be greater than zero."
            );
        }

        Product product = getProductById(productId);

        if (amount > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Cannot remove more stock than is currently available."
            );
        }

        product.setQuantity(product.getQuantity() - amount);

        productRepository.update(product);
    }

    public boolean isLowStock(Product product) {
        return product.isLowStock();
    }

    public BigDecimal calculateInventoryValue() {
        return getAllProducts()
                .stream()
                .map(product ->
                        product.getPrice()
                                .multiply(
                                        BigDecimal.valueOf(
                                                product.getQuantity()
                                        )
                                )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null."
            );
        }

        if (product.getName() == null
                || product.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Product name cannot be empty."
            );
        }

        if (product.getSku() == null
                || product.getSku().isBlank()) {
            throw new IllegalArgumentException(
                    "SKU cannot be empty."
            );
        }

        if (product.getCategory() == null) {
            throw new IllegalArgumentException(
                    "Product category must be selected."
            );
        }

        if (product.getPrice() == null
                || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Price cannot be negative."
            );
        }

        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be negative."
            );
        }

        if (product.getLowStockThreshold() < 0) {
            throw new IllegalArgumentException(
                    "Low-stock threshold cannot be negative."
            );
        }
    }

}