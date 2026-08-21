package com.mikekent.stocktrack.model;

import java.math.BigDecimal;

public class Product {

    private int id;
    private String name;
    private String sku;
    private Category category;
    private BigDecimal price;
    private int quantity;
    private int lowStockThreshold;

    public Product(
            int id,
            String name,
            String sku,
            Category category,
            BigDecimal price,
            int quantity,
            int lowStockThreshold) {

        this.id = id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public Product(
            String name,
            String sku,
            Category category,
            BigDecimal price,
            int quantity,
            int lowStockThreshold) {

        this(
                0,
                name,
                sku,
                category,
                price,
                quantity,
                lowStockThreshold
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
    
}