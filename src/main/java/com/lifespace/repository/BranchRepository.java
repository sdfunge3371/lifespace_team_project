package com.lifespace.repository;

import com.lifespace.entity.Branch;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, String> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE branch SET branchstatus = ?1 WHERE branch_id = ?2", nativeQuery = true)
    void updateBranchStatusByBranchId(Integer branchStatus, String branchId);

    @EntityGraph(attributePaths = {"publicEquipments"})
    List<Branch> findAll();

    List<Branch> findByBranchStatus(Integer branchStatus);

    List<Branch> findByBranchIdContaining(String branchId);

    List<Branch> findByBranchNameContaining(String branchName);
}