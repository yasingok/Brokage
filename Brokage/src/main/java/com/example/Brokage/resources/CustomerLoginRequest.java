package com.example.Brokage.resources;

import lombok.Data;

@Data
public class CustomerLoginRequest {
    private String customerId;
    private String password;
}
