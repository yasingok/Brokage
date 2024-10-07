package com.example.Brokage.resources;

import lombok.Data;

import java.util.List;
@Data
public class OrderListResponse {
    private List<OrderResponse> orders;

    public OrderListResponse(List<OrderResponse> orders) {
        this.orders = orders;
    }

    // Getters and Setters
}
