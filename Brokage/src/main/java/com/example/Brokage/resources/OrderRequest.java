package com.example.Brokage.resources;

import com.example.Brokage.enums.Side;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OrderRequest implements Serializable {
    @NotBlank(message = "customerId should be set")
    private Long customerId;
    @NotBlank(message = "assetName should be set")
    private String assetName;
    @NotBlank(message = "orderSide should be set")
    private Side orderSide;
    @NotBlank(message = "size should be set")
    private int size;
    @NotBlank(message = "price should be set")
    private double price;
}
