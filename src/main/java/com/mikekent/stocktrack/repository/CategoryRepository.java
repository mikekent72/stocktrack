package com.mikekent.stocktrack.repository;

import com.mikekent.stocktrack.database.DatabaseManager;
import com.mikekent.stocktrack.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    public Category save(Category category) {
        String sql = """
                INSERT INTO categories (name)
                VALUES (?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, category.getName());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
            }

            return category;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not save category.",
                    e
            );
        }
    }

    public List<Category> findAll() {
        String sql = """
                SELECT id, name
                FROM categories
                ORDER BY name
                """;

        List<Category> categories = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(mapRow(resultSet));
            }

            return categories;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not retrieve categories.",
                    e
            );
        }
    }

    public Category findById(int id) {
        String sql = """
                SELECT id, name
                FROM categories
                WHERE id = ?
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
                    "Could not retrieve category.",
                    e
            );
        }
    }

    public Category findByName(String name) {
        String sql = """
                SELECT id, name
                FROM categories
                WHERE name = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not retrieve category.",
                    e
            );
        }
    }

    public void update(Category category) {
        String sql = """
                UPDATE categories
                SET name = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, category.getName());
            statement.setInt(2, category.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not update category.",
                    e
            );
        }
    }

    public void deleteById(int id) {
        String sql = """
                DELETE FROM categories
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not delete category.",
                    e
            );
        }
    }

    private Category mapRow(ResultSet resultSet) throws SQLException {
        return new Category(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }
    
}