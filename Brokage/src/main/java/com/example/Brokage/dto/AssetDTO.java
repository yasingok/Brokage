package com.example.Brokage.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class AssetDTO extends BaseDTO{
    private int usableSize;

    // Getters and Setters
}
