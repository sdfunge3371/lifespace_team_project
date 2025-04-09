package com.lifespace.repository;

import com.lifespace.entity.RentalItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RentalItemRepository extends JpaRepository<RentalItem, String> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE rental_item SET rental_item_status = ?1 WHERE rental_item_id = ?2", nativeQuery = true)
    void updateRentalItemStatusByRentalItemId(Integer rentalItemStatus, String rentalItemId);

    List<RentalItem> findByRentalItemStatus(Integer rentalItemStatus);

    List<RentalItem> findByRentalItemIdContaining(String rentalItemId);

    List<RentalItem> findByRentalItemNameContaining(String rentalItemName);
    
    List<RentalItem> findByBranch_BranchId(String branchId);
}