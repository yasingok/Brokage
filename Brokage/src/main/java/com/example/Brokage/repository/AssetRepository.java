package com.example.Brokage.repository;

import com.example.Brokage.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByCustomerId(Long customerId);
    Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);

}
