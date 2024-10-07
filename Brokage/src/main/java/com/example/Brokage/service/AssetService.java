package com.example.Brokage.service;

import com.example.Brokage.exception.AssetBaseException;
import com.example.Brokage.model.Asset;
import com.example.Brokage.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {
    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);
    private final AssetRepository assetRepository;

    public void depositMoney(Long customerId, int amount) {
        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")
                .orElseGet(() -> {
                    logger.warn("TRY asset not found for customer, customerId:{}", customerId);
                    // Create a new asset if not found
                    Asset newAsset = new Asset();
                    newAsset.setCustomerId(customerId);
                    newAsset.setAssetName("TRY");
                    newAsset.setUsableSize(amount); // Initialize with the deposited amount
                    newAsset.setSize(amount); // Set total size as the deposited amount
                    return newAsset;
                });

        asset.setUsableSize(asset.getUsableSize() + amount);
        asset.setSize(asset.getSize() + amount);

        assetRepository.save(asset);
    }

    public void withdrawMoney(Long customerId, int amount, String iban) throws AssetBaseException {
        // Implementation for withdrawing money
        // Find the customer's TRY asset
        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY")
                .orElseThrow(() -> {
                    logger.error("TRY asset not found for customer, customerId:{}", customerId);
                    return new AssetBaseException("TRY asset not found for customer");
                });

        // Check if there is sufficient usable size
        if (asset.getUsableSize() < amount) {
            logger.error("Insufficient funds for withdrawal, customerId:{}", customerId);
            throw new AssetBaseException("Insufficient funds for withdrawal.");
        }

        asset.setUsableSize(asset.getUsableSize() - amount);
        asset.setSize(asset.getSize() - amount);
        assetRepository.save(asset);
        // Logic to handle the actual transfer of money to the given IBAN would go here
    }

    public List<Asset> listAssets(Long customerId) throws AssetBaseException {
        List<Asset> assets = assetRepository.findByCustomerId(customerId);

        if (assets.isEmpty()) {
            logger.error("No assets found for customer, customerId: {}", customerId);
            throw new AssetBaseException("No assets found for customer");
        }

        return assets;
    }
}
