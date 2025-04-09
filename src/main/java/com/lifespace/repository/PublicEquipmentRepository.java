package com.lifespace.repository;

import com.lifespace.entity.PublicEquipment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PublicEquipmentRepository extends JpaRepository<PublicEquipment, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public_equipment WHERE branch_id = ?1", nativeQuery = true)
    void deleteByBranchId(String branchId);
    
    @Query(value = "SELECT * FROM public_equipment WHERE branch_id = ?1", nativeQuery = true)
    List<PublicEquipment> findByBranchId(String branchId);
}