package com.example.Brokage.controller;

import com.example.Brokage.enums.Role;
import com.example.Brokage.exception.AssetBaseException;
import com.example.Brokage.exception.InvalidInputException;
import com.example.Brokage.model.Asset;
import com.example.Brokage.model.Customer;
import com.example.Brokage.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {
    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);
    private final AssetService assetService;

    @PostMapping("/deposit")
    public ResponseEntity<Void> depositMoney(@AuthenticationPrincipal Customer customer,
                                             @RequestParam @NotEmpty(message = "customerId should be set") Long customerId,
                                             @RequestParam @NotEmpty(message = "amount should be set") int amount) throws InvalidInputException, AssetBaseException {
        logger.trace("Deposit money , customerId:{}", customerId);
        if ( (!Objects.equals(customer.getCustomerId(), customerId.toString())) && customer.getRole() == Role.CUSTOMER){
            logger.error("customerId or token is not belong this customer , customerId:{}", customerId);
            throw new InvalidInputException("customerId or token is not belong this customer");
        }
        assetService.depositMoney(customerId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdrawMoney(@AuthenticationPrincipal Customer customer,
                                              @RequestParam @NotEmpty(message = "customerId should be set") Long customerId,
                                              @RequestParam @NotEmpty(message = "amount should be set") int amount,
                                              @RequestParam @NotEmpty(message = "iban should be set") String iban) throws InvalidInputException, AssetBaseException {
        logger.trace("Withdraw money , customerId:{}", customerId);
        if ( (!Objects.equals(customer.getCustomerId(), customerId.toString())) && customer.getRole() == Role.CUSTOMER){
            logger.error("customerId or token is not belong this customer , customerId:{}", customerId);
            throw new InvalidInputException("customerId or token is not belong this customer");
        }
        assetService.withdrawMoney(customerId, amount, iban);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Asset>> listAssets(@AuthenticationPrincipal Customer customer,
                                                  @RequestParam Long customerId) throws InvalidInputException, AssetBaseException {
        logger.trace("Get All Assets , customerId:{}", customerId);
        if ( (!Objects.equals(customer.getCustomerId(), customerId.toString())) && customer.getRole() == Role.CUSTOMER){
            logger.error("customerId or token is not belong this customer , customerId:{}", customerId);
            throw new InvalidInputException("customerId or token is not belong this customer");
        }
        return ResponseEntity.ok(assetService.listAssets(customerId));
    }
}
