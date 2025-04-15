package com.lifespace.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lifespace.dto.SpaceCommentResponse;
import com.lifespace.entity.Space;

public interface SpaceRepository extends JpaRepository<Space, String> {
    // JpaRepository 內建：
    // - save(User user)
    // - findById(Integer id)
    // - findAll()
    // - deleteById(Integer id)

    Optional<Space> findBySpaceName(String spaceName);

    List<Space> findBySpaceNameContainingIgnoreCase(String keyword);


  //依照條件篩選空間評論
    @Query(value = "SELECT o.order_id, o.space_id, s.space_name AS space_name, s.branch_id AS branch_id, m.member_name AS member_name, "+
    		"m.member_image AS member_image, o.comment_content, o.satisfaction, o.comment_time, " +
            "GROUP_CONCAT(sp.space_photo) AS photos_urls, sc.comment_reply_content AS comment_reply " +
            "FROM orders o " +
            "LEFT JOIN space_comment_photo sp ON sp.order_id = o.order_id " +
            "LEFT JOIN space s ON s.space_id = o.space_id " +
            "LEFT JOIN member m ON m.member_id = o.member_id " +
            "LEFT JOIN space_comment_reply sc ON sc.order_id = o.order_id " +
            "WHERE (:spaceId IS NULL OR o.space_id = :spaceId ) " +
            "AND (:spaceName IS NULL OR s.space_name LIKE CONCAT('%', :spaceName, '%') ) " +
            "AND (:branchId IS NULL OR s.branch_id = :branchId ) " +
            "AND ( o.comment_content IS NOT NULL ) " +
            "GROUP BY o.order_id, o.space_id, o.comment_content, o.satisfaction, o.comment_time, m.member_name, m.member_image, sc.comment_reply_content",
            countQuery = "SELECT o.order_id, o.space_id, s.space_name AS space_name, s.branch_id AS branch_id, m.member_name AS member_name, "+
            		"m.member_image AS member_image, o.comment_content, o.satisfaction, o.comment_time, " +
                    "GROUP_CONCAT(sp.space_photo) AS photos_urls, sc.comment_reply_content " +
                    "FROM orders o " +
                    "LEFT JOIN space_comment_photo sp ON sp.order_id = o.order_id " +
                    "LEFT JOIN space s ON s.space_id = o.space_id " +
                    "LEFT JOIN member m ON m.member_id = o.member_id " +
                    "LEFT JOIN space_comment_reply sc ON sc.order_id = o.order_id " +
                    "WHERE (:spaceId IS NULL OR o.space_id = :spaceId ) " +
                    "AND (:spaceName IS NULL OR s.space_name LIKE CONCAT('%', :spaceName, '%') ) " +
                    "AND (:branchId IS NULL OR s.branch_id = :branchId ) " +
                    "AND ( o.comment_content IS NOT NULL ) " +
                    "GROUP BY o.order_id, o.space_id, o.comment_content, o.satisfaction, o.comment_time, m.member_name, m.member_image, sc.comment_reply_content",
            nativeQuery = true)
    Page<SpaceCommentResponse> findSpaceCommentsByConditions(
            @Param("spaceId") String spaceId,
            @Param("spaceName") String spaceName,
            @Param("branchId") String branchId,
            Pageable pageable);


    // for 睿寓: 計算空間的平均滿意度
    @Query("SELECT AVG(o.satisfaction) FROM Orders o WHERE o.space.spaceId = :spaceId AND o.commentContent IS NOT NULL")
    Double findAverageSatisfactionBySpaceId(@Param("spaceId") String spaceId);

    // 關鍵字、開始結束時間的複合查詢
    @Query("""
            SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
            FROM Orders o
            WHERE o.spaceId = :spaceId
            AND (
                (:startDateTime BETWEEN o.orderStart AND o.orderEnd)
                OR (:endDateTime BETWEEN o.orderStart AND o.orderEnd)
                OR (o.orderStart BETWEEN :startDateTime AND :endDateTime)
            )
    """)
    boolean existsBySpaceIdAndOrderOverlap(@Param("spaceId") String spaceId,
                                           @Param("startDateTime") LocalDateTime start,
                                           @Param("endDateTime") LocalDateTime end);

}