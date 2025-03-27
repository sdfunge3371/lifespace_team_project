package com.lifespace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.lifespace.entity.BranchVO;

public interface BranchRepository extends JpaRepository<BranchVO, String> {
    
    @Transactional
    @Modifying
    @Query(value = "delete from branch where branch_id = ?1", nativeQuery = true)
    void deleteByBranchId(String branchId);
    
    // 自訂條件查詢 - 依照分店名稱
    @Query(value = "from BranchVO where branchName like %?1%")
    List<BranchVO> findByBranchName(String branchName);
    
    // 自訂條件查詢 - 依照分店地址
    @Query(value = "from BranchVO where branchAddr like %?1%")
    List<BranchVO> findByBranchAddr(String branchAddr);
    
    // 自訂條件查詢 - 依照分店狀態
    @Query(value = "from BranchVO where branchStatus = ?1")
    List<BranchVO> findByBranchStatus(Integer branchStatus);
    
    // 自訂複合條件查詢
    @Query(value = "from BranchVO where branchId = ?1 or branchName like %?2%")
    List<BranchVO> findByIdOrName(String branchId, String branchName);
}