package com.example.Brokage.resources;

import com.example.Brokage.enums.Side;
import com.example.Brokage.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long customerId;
    private String assetName;
    private Side orderSide;
    private int size;
    private double price;
    private Status status;
}
