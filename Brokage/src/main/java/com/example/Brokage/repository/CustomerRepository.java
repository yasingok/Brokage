package com.example.Brokage.repository;

import com.example.Brokage.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByCustomerId(String customerId);
}
