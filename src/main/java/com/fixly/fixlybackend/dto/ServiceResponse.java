package com.fixly.fixlybackend.dto;

import com.fixly.fixlybackend.model.ServiceCategory;

public class ServiceResponse {

    private Long id;
    private String name;
    private String description;
    private double price;
    private ServiceCategory category;

    public ServiceResponse(
            Long id,
            String name,
            String description,
            double price,
            ServiceCategory category) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public ServiceCategory getCategory() {
        return category;
    }
}