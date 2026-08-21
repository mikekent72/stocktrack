package com.mikekent.stocktrack.database;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitialiser {

    public static void initialise() {
        createDataDirectory();
        createTables();
    }

    private static void createDataDirectory() {
        File dataDirectory = new File("data");

        if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
            throw new IllegalStateException("Could not create data directory.");
        }
    }

    private static void createTables() {
        String createCategoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
                """;

        String createProductsTable = """
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    sku TEXT NOT NULL UNIQUE,
                    category_id INTEGER NOT NULL,
                    price NUMERIC NOT NULL,
                    quantity INTEGER NOT NULL,
                    low_stock_threshold INTEGER NOT NULL,
                    FOREIGN KEY (category_id) REFERENCES categories(id)
                )
                """;

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(createCategoriesTable);
            statement.execute(createProductsTable);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not initialise the database.",
                    e
            );
        }
    }

}