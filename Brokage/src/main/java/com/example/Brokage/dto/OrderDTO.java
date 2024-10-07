package com.example.Brokage.dto;

import com.example.Brokage.enums.Side;
import com.example.Brokage.enums.Status;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class OrderDTO extends BaseDTO{
    private Long id;
    private Side orderSide;
    private double price;
    private Status status;
}
