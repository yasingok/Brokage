package com.example.Brokage.repository;

import com.example.Brokage.enums.Side;
import com.example.Brokage.enums.Status;
import com.example.Brokage.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);
    List<Order> findByOrderSideAndStatus(Side orderSide, Status status);
}
