package com.example.Brokage.controller;

import com.example.Brokage.converter.OrderConverter;
import com.example.Brokage.enums.Role;
import com.example.Brokage.exception.InvalidInputException;
import com.example.Brokage.exception.OrderBaseException;
import com.example.Brokage.model.Customer;
import com.example.Brokage.resources.OrderListResponse;
import com.example.Brokage.resources.OrderRequest;
import com.example.Brokage.resources.OrderResponse;
import com.example.Brokage.service.OrderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal Customer customer, @RequestBody @NotNull @Valid OrderRequest orderRequest) throws InvalidInputException, OrderBaseException {
        logger.trace("Creating Order, customerId:{}", orderRequest.getCustomerId());

        if ( (!Objects.equals(customer.getCustomerId(), orderRequest.getCustomerId().toString())) && customer.getRole() == Role.CUSTOMER){
            logger.error("customerId or token is not belong this customer , customerId:{}", orderRequest.getCustomerId());
            throw new InvalidInputException("customerId or token is not belong this customer");
        }

        OrderResponse orderResponse =
                OrderConverter.ToOrderResponse(orderService.createOrder(OrderConverter.ToOrderDto(orderRequest)));
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping
    public ResponseEntity<OrderListResponse> listOrders(@AuthenticationPrincipal Customer customer,
                                                        @RequestParam @NotEmpty(message = "customerId should be set") Long customerId,
                                                        @RequestParam @NotEmpty(message = "startDate should be set") LocalDateTime startDate,
                                                        @RequestParam @NotEmpty(message = "endDate should be set") LocalDateTime endDate) throws InvalidInputException, OrderBaseException {
        logger.trace("Getting Order, customerId:{}", customerId);
        if ( (!Objects.equals(customer.getCustomerId(), customerId.toString())) && customer.getRole() == Role.CUSTOMER){
            logger.error("customerId or token is not belong this customer , customerId:{}", customerId);
            throw new InvalidInputException("customerId or token is not belong this customer");
        }
        List<OrderResponse> orders  = orderService.listOrders(customerId, startDate, endDate).stream()
                .map(OrderConverter::ToOrderResponse).toList();
        return ResponseEntity.ok(new OrderListResponse(orders));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteOrder(@AuthenticationPrincipal Customer customer,
                                            @RequestParam @NotEmpty(message = "id should be set") Long id,
                                            @RequestParam @NotEmpty(message = "customerId should be set") Long customerId) throws InvalidInputException, OrderBaseException {
        logger.trace("Delete Order, customerId:{}", customerId);
        if ( (!Objects.equals(customer.getCustomerId(), customerId.toString())) && customer.getRole() == Role.CUSTOMER){
            logger.error("customerId or token is not belong this customer , customerId:{}", customerId);
            throw new InvalidInputException("customerId or token is not belong this customer");
        }
        orderService.deleteOrder(id, customerId);
        return ResponseEntity.noContent().build();
    }
}
