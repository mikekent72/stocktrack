package com.mikekent.stocktrack.repository;

import com.mikekent.stocktrack.database.DatabaseManager;
import com.mikekent.stocktrack.model.Category;
import com.mikekent.stocktrack.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    public Product save(Product product) {
        String sql = """
                INSERT INTO products (
                    name,
                    sku,
                    category_id,
                    price,
                    quantity,
                    low_stock_threshold
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getSku());
            statement.setInt(3, product.getCategory().getId());
            statement.setBigDecimal(4, product.getPrice());
            statement.setInt(5, product.getQuantity());
            statement.setInt(6, product.getLowStockThreshold());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
            }

            return product;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not save product.",
                    e
            );
        }
    }

    public Product findById(int id) {
        String sql = """
                SELECT
                    p.id,
                    p.name,
                    p.sku,
                    p.price,
                    p.quantity,
                    p.low_stock_threshold,
                    c.id AS category_id,
                    c.name AS category_name
                FROM products p
                JOIN categories c ON p.category_id = c.id
                WHERE p.id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not retrieve product.",
                    e
            );
        }
    }

    public Product findBySku(String sku) {
        String sql = """
                SELECT
                    p.id,
                    p.name,
                    p.sku,
                    p.price,
                    p.quantity,
                    p.low_stock_threshold,
                    c.id AS category_id,
                    c.name AS category_name
                FROM products p
                JOIN categories c ON p.category_id = c.id
                WHERE p.sku = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, sku);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not retrieve product.",
                    e
            );
        }
    }

    public List<Product> findAll() {
        String sql = """
                SELECT
                    p.id,
                    p.name,
                    p.sku,
                    p.price,
                    p.quantity,
                    p.low_stock_threshold,
                    c.id AS category_id,
                    c.name AS category_name
                FROM products p
                JOIN categories c ON p.category_id = c.id
                ORDER BY p.name
                """;

        return executeProductQuery(sql);
    }

    public List<Product> search(String searchTerm) {
        String sql = """
                SELECT
                    p.id,
                    p.name,
                    p.sku,
                    p.price,
                    p.quantity,
                    p.low_stock_threshold,
                    c.id AS category_id,
                    c.name AS category_name
                FROM products p
                JOIN categories c ON p.category_id = c.id
                WHERE LOWER(p.name) LIKE LOWER(?)
                   OR LOWER(p.sku) LIKE LOWER(?)
                ORDER BY p.name
                """;

        String pattern = "%" + searchTerm + "%";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pattern);
            statement.setString(2, pattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapRows(resultSet);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not search products.",
                    e
            );
        }
    }

    public List<Product> findByCategoryId(int categoryId) {
        String sql = """
                SELECT
                    p.id,
                    p.name,
                    p.sku,
                    p.price,
                    p.quantity,
                    p.low_stock_threshold,
                    c.id AS category_id,
                    c.name AS category_name
                FROM products p
                JOIN categories c ON p.category_id = c.id
                WHERE p.category_id = ?
                ORDER BY p.name
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapRows(resultSet);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not retrieve products by category.",
                    e
            );
        }
    }

    public List<Product> findLowStock() {
        String sql = """
                SELECT
                    p.id,
                    p.name,
                    p.sku,
                    p.price,
                    p.quantity,
                    p.low_stock_threshold,
                    c.id AS category_id,
                    c.name AS category_name
                FROM products p
                JOIN categories c ON p.category_id = c.id
                WHERE p.quantity <= p.low_stock_threshold
                ORDER BY p.quantity, p.name
                """;

        return executeProductQuery(sql);
    }

    public void update(Product product) {
        String sql = """
                UPDATE products
                SET name = ?,
                    sku = ?,
                    category_id = ?,
                    price = ?,
                    quantity = ?,
                    low_stock_threshold = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getSku());
            statement.setInt(3, product.getCategory().getId());
            statement.setBigDecimal(4, product.getPrice());
            statement.setInt(5, product.getQuantity());
            statement.setInt(6, product.getLowStockThreshold());
            statement.setInt(7, product.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not update product.",
                    e
            );
        }
    }

    public void deleteById(int id) {
        String sql = """
                DELETE FROM products
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not delete product.",
                    e
            );
        }
    }

    private List<Product> executeProductQuery(String sql) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            return mapRows(resultSet);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not retrieve products.",
                    e
            );
        }
    }

    private List<Product> mapRows(ResultSet resultSet) throws SQLException {
        List<Product> products = new ArrayList<>();

        while (resultSet.next()) {
            products.add(mapRow(resultSet));
        }

        return products;
    }

    private Product mapRow(ResultSet resultSet) throws SQLException {
        Category category = new Category(
                resultSet.getInt("category_id"),
                resultSet.getString("category_name")
        );

        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("sku"),
                category,
                resultSet.getBigDecimal("price"),
                resultSet.getInt("quantity"),
                resultSet.getInt("low_stock_threshold")
        );
    }
    
}