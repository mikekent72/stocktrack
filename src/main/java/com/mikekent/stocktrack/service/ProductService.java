package com.mikekent.stocktrack.service;

import com.mikekent.stocktrack.exception.DuplicateSkuException;
import com.mikekent.stocktrack.exception.InsufficientStockException;
import com.mikekent.stocktrack.exception.InvalidProductException;
import com.mikekent.stocktrack.exception.ProductNotFoundException;
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
            throw new DuplicateSkuException(
                    "A product with this SKU already exists."
            );
        }

        return productRepository.save(product);
    }

    public Product getProductById(int id) {
        Product product = productRepository.findById(id);

        if (product == null) {
            throw new ProductNotFoundException(
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
            throw new DuplicateSkuException(
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
            throw new InvalidProductException(
                    "Stock amount must be greater than zero."
            );
        }

        Product product = getProductById(productId);

        product.setQuantity(product.getQuantity() + amount);

        productRepository.update(product);
    }

    public void removeStock(int productId, int amount) {
        if (amount <= 0) {
            throw new InvalidProductException(
                    "Stock amount must be greater than zero."
            );
        }

        Product product = getProductById(productId);

        if (amount > product.getQuantity()) {
            throw new InsufficientStockException(
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
            throw new InvalidProductException(
                    "Product cannot be null."
            );
        }

        if (product.getName() == null
                || product.getName().isBlank()) {
            throw new InvalidProductException(
                    "Product name cannot be empty."
            );
        }

        if (product.getSku() == null
                || product.getSku().isBlank()) {
            throw new InvalidProductException(
                    "SKU cannot be empty."
            );
        }

        if (product.getCategory() == null) {
            throw new InvalidProductException(
                    "Product category must be selected."
            );
        }

        if (product.getPrice() == null
                || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductException(
                    "Price cannot be negative."
            );
        }

        if (product.getQuantity() < 0) {
            throw new InvalidProductException(
                    "Quantity cannot be negative."
            );
        }

        if (product.getLowStockThreshold() < 0) {
            throw new InvalidProductException(
                    "Low-stock threshold cannot be negative."
            );
        }
    }
}