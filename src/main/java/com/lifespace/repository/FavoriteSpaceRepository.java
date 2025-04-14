package com.lifespace.repository;

import com.lifespace.entity.FavoriteSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteSpaceRepository extends JpaRepository<FavoriteSpace, Integer> {
    
    // 根據會員ID查詢收藏的空間列表
    List<FavoriteSpace> findByMemberId(String memberId);
    
    // 根據空間ID查詢收藏此空間的會員列表
    List<FavoriteSpace> findBySpaceId(String spaceId);
    
    // 根據會員ID和空間ID查詢是否已收藏
    Optional<FavoriteSpace> findByMemberIdAndSpaceId(String memberId, String spaceId);
    
    // 根據會員ID和空間ID刪除收藏記錄
    void deleteByMemberIdAndSpaceId(String memberId, String spaceId);
    
    // 檢查會員是否有收藏任何空間
    boolean existsByMemberId(String memberId);
    
    // 查詢會員收藏的空間數量
    @Query("SELECT COUNT(fs) FROM FavoriteSpace fs WHERE fs.memberId = :memberId")
    long countByMemberId(@Param("memberId") String memberId);
    
    // 根據會員ID查詢收藏的空間ID列表
    @Query("SELECT fs.spaceId FROM FavoriteSpace fs WHERE fs.memberId = :memberId")
    List<String> findSpaceIdsByMemberId(@Param("memberId") String memberId);
}