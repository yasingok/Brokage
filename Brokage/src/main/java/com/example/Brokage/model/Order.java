package com.example.Brokage.model;

import com.example.Brokage.enums.Side;
import com.example.Brokage.enums.Status;
import jakarta.persistence.*;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders") // Use a different name for the table
public class Order extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String assetName;

    @Enumerated(EnumType.STRING)
    private Side orderSide;
    private int size;
    private double price;

    @Enumerated(EnumType.STRING)
    private Status status;
}
