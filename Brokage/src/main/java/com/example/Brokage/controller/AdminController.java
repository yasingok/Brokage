package com.example.Brokage.controller;

import com.example.Brokage.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final OrderService orderService;

    @Autowired
    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/match-orders")
    public ResponseEntity<String> matchOrders() {
        logger.error("Match Orders process");
        orderService.matchPendingOrders();
        return ResponseEntity.ok("Pending orders matched successfully.");
    }
}
