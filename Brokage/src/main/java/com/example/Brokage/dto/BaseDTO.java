package com.example.Brokage.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
@Data
@SuperBuilder(toBuilder = true)
public abstract class BaseDTO implements Serializable {
    private Long customerId;
    private String assetName;
    private int size;
}
