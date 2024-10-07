package com.example.Brokage.service;

import com.example.Brokage.controller.OrderController;
import com.example.Brokage.converter.OrderConverter;
import com.example.Brokage.dto.OrderDTO;
import com.example.Brokage.enums.Side;
import com.example.Brokage.enums.Status;
import com.example.Brokage.exception.OrderBaseException;
import com.example.Brokage.model.Asset;
import com.example.Brokage.model.Order;
import com.example.Brokage.repository.AssetRepository;
import com.example.Brokage.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    public OrderDTO createOrder(OrderDTO orderDTO) throws OrderBaseException {
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(orderDTO.getCustomerId(), "TRY")
                .orElseThrow(() -> {
                    logger.error("TRY asset not found for customer, customerId:{}", orderDTO.getCustomerId());
                    return new OrderBaseException("TRY asset not found for customer");});

        // Handling the order based on the order side (BUY/SELL)
        if (orderDTO.getOrderSide() == Side.SELL) {
            Asset trySellAsset = assetRepository.findByCustomerIdAndAssetName(orderDTO.getCustomerId(), orderDTO.getAssetName())
                                .orElseThrow(() -> {
                                    logger.error("{} asset not found for customer, customerId:{}", orderDTO.getAssetName(),orderDTO.getAssetName());
                                    return new OrderBaseException("asset not found for customer");});
            // Check if the customer has enough asset to sell
            if (trySellAsset.getUsableSize() < orderDTO.getSize()) {
                logger.error("Insufficient assets for this sell order, customerId:{}", orderDTO.getCustomerId());
                throw new OrderBaseException("Insufficient assets for this sell order.");
            }

            tryAsset.setUsableSize(tryAsset.getUsableSize() - orderDTO.getSize());
        }else {
            if (tryAsset.getUsableSize() < orderDTO.getSize() * orderDTO.getPrice()) {
                logger.error("Insufficient funds for this order, customerId:{}", orderDTO.getCustomerId());
                throw new OrderBaseException("Insufficient funds for this order.");
            }
            tryAsset.setUsableSize((int) (tryAsset.getUsableSize() - (orderDTO.getSize() * orderDTO.getPrice())));
        }

        Order order = Order.builder()
                .customerId(orderDTO.getCustomerId())
                .assetName(orderDTO.getAssetName())
                .orderSide(orderDTO.getOrderSide())
                .size(orderDTO.getSize())
                .price(orderDTO.getPrice())
                .status(Status.PENDING).build();
        orderRepository.save(order);
        // Update the usable size of TRY asset
        assetRepository.save(tryAsset);
        return OrderConverter.ToOrderDto(order);
    }

    public List<OrderDTO> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) throws OrderBaseException {
        List<Order> orders = orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);

        if (orders.isEmpty()) {
            logger.error("No orders found for customer, customerId: {}", customerId);
            throw new OrderBaseException("No orders found for customer");
        }

        return orders.stream()
                .map(OrderConverter::ToOrderDto)
                .toList();
    }

    public void deleteOrder(Long orderId, Long customerId) throws OrderBaseException {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> {
                    logger.error("Order not found for this customer, customerId:{}", customerId);
                    return new OrderBaseException("Order not found for this customer.");
                });

        if (order.getStatus() != Status.PENDING) {
            logger.error("Only pending orders can be deleted, customerId:{}", customerId);
            throw new OrderBaseException("Only pending orders can be deleted.");
        }

        order.setStatus(Status.CANCELLED);
        // Update the usable size of TRY asset
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")
                .orElseThrow(() -> {
                    logger.error("TRY asset not found for customer, customerId:{}", customerId);
                    return new RuntimeException("TRY asset not found for customer.");
                });
        tryAsset.setUsableSize((int) (tryAsset.getUsableSize() + (order.getSize() * order.getPrice())));
        assetRepository.save(tryAsset);

        // Delete the order
        orderRepository.save(order);
    }

    public void matchPendingOrders() {
        List<Order> pendingBuyOrders = orderRepository.findByOrderSideAndStatus(Side.BUY, Status.PENDING);
        List<Order> pendingSellOrders = orderRepository.findByOrderSideAndStatus(Side.SELL, Status.PENDING);

        for (Order buyOrder : pendingBuyOrders) {
            Optional<Asset> tryAssetOpt = assetRepository.findByCustomerIdAndAssetName(buyOrder.getCustomerId(), "TRY");

            if (tryAssetOpt.isEmpty()){
                continue;
            }
            Asset tryAsset = tryAssetOpt.get();
            if (tryAsset.getUsableSize() < buyOrder.getSize() * buyOrder.getPrice()) {
                continue;
            }

            for (Order sellOrder : pendingSellOrders) {
                if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                    int matchedSize = Math.min(buyOrder.getSize(), sellOrder.getSize());
                    updateAssetsAfterMatch(buyOrder, sellOrder, matchedSize);

                    buyOrder.setStatus(Status.MATCHED);
                    sellOrder.setStatus(Status.MATCHED);
                    orderRepository.save(buyOrder);
                    orderRepository.save(sellOrder);

                    break;
                }
            }
        }
    }

    private void updateAssetsAfterMatch(Order buyOrder, Order sellOrder, int size) {
        // Update the TRY asset for the buyer
        Asset buyAsset = assetRepository.findByCustomerIdAndAssetName(buyOrder.getCustomerId(), "TRY").get();
        buyAsset.setUsableSize( buyAsset.getUsableSize() - (size * buyOrder.getPrice())); // Decrease by total price
        assetRepository.save(buyAsset);

        // Update the asset for the seller
        Optional<Asset> sellAssetOpt = assetRepository.findByCustomerIdAndAssetName(sellOrder.getCustomerId(), sellOrder.getAssetName());
        if (sellAssetOpt.isEmpty()){
            logger.error("TRY asset not found for customer, customerId:{}, assetName:{}", sellOrder.getCustomerId(),sellOrder.getAssetName());
        }
        Asset sellAsset = sellAssetOpt.get();
        sellAsset.setUsableSize(sellAsset.getUsableSize() + size); // Increase sold asset's usable size
        assetRepository.save(sellAsset);
    }

}
