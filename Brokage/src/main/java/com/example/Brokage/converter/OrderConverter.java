package com.example.Brokage.converter;

import com.example.Brokage.dto.OrderDTO;
import com.example.Brokage.model.Order;
import com.example.Brokage.resources.OrderRequest;
import com.example.Brokage.resources.OrderResponse;

public class OrderConverter {
    public static OrderDTO ToOrderDto(OrderRequest orderRequest){
        return OrderDTO.builder()
                .customerId(orderRequest.getCustomerId())
                .assetName(orderRequest.getAssetName())
                .size(orderRequest.getSize())
                .orderSide(orderRequest.getOrderSide())
                .price(orderRequest.getPrice()).build();
    }

    public static OrderDTO ToOrderDto(Order order){
        return OrderDTO.builder()
                .customerId(order.getCustomerId())
                .assetName(order.getAssetName())
                .size(order.getSize())
                .id(order.getId())
                .status(order.getStatus())
                .orderSide(order.getOrderSide())
                .price(order.getPrice()).build();
    }

    public static OrderResponse ToOrderResponse(OrderDTO orderDTO){
        return OrderResponse.builder()
                .id(orderDTO.getId())
                .customerId(orderDTO.getCustomerId())
                .assetName(orderDTO.getAssetName())
                .orderSide(orderDTO.getOrderSide())
                .size(orderDTO.getSize())
                .price(orderDTO.getPrice())
                .status(orderDTO.getStatus()).build();
    }
}
