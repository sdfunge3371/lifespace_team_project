package com.lifespace.repository;

import com.lifespace.entity.Orders;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


public interface OrdersRepository extends JpaRepository<Orders, String> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET order_status = ?1 WHERE order_id = ?2", nativeQuery = true)
    void updateOrderStatusByOrderId(Integer orderStatus, String orderId);

    //排程器用(找結束時間在現在之前的訂單狀態)
    List<Orders> findByOrderStatusAndOrderEndBefore(Integer orderStatus, Timestamp OrderEnd );

    //先把這些都join好可以直接get.
    @EntityGraph(attributePaths = {"branch", "member", "rentalItemDetails", "rentalItemDetails.rentalItem"})
    List<Orders> findAll();

    //查所有訂單狀態是已啟用的會員編號
    @Query("SELECT o FROM Orders o WHERE o.member.memberId = :memberId And o.member.accountStatus = 1")
    List<Orders> findAllByMemberId(@Param("memberId") String memberId);

    //line官方第一次用memberId查歷史訂單(綁定lineUserId用)
    List<Orders> findByMemberId(String memberId);

    //line官方第二次之後用lineUserId查三筆已付款訂單
    List<Orders> findTop3ByLineUserIdAndOrderStatusOrderByOrderStartDesc(String lineUserId, Integer orderStatus);

    //判斷訂單是否有存LineUserId
    boolean existsByLineUserId(String lineUserId);

    //更新所有裡面lineUserId是null的訂單編號(給會員綁定用)
    @Transactional
    @Modifying
    @Query("UPDATE Orders o Set o.lineUserId = :lineUserId WHERE o.orderId IN :orderIds AND o.lineUserId IS NULL")
    int bulkInsertLineUserIdIfNull(@Param("lineUserId") String lineUserId, @Param("orderIds") List<String> orderIds);

    //查詢該訂單編號裡的訂單狀態回傳
    @Query("SELECT o.orderStatus FROM Orders o WHERE o.orderId = :orderId ")
    Integer findOrderStatusByOrderId(@Param("orderId")  String orderId);

    //查詢該會員ID且有LineUserId的最近開始時間訂單
    @Query("SELECT o.lineUserId FROM Orders o WHERE o.member.memberId = :memberId AND o.lineUserId IS NOT NULL ORDER BY o.orderStart DESC")
    List<String> findTopLineUserIdByMemberId(@Param("memberId") String memberId);

    //用舉辦人id以及活動id查詢訂單，作為舉辦者取消活動用
    Optional<Orders> findByEventEventIdAndMemberMemberId(String eventId, String memberId);

    // 根據空間抓該空間已經被預訂的所有時段
    @Query("SELECT o FROM Orders o WHERE o.space.spaceId = :spaceId AND FUNCTION('DATE', o.orderStart) = :date AND o.orderStatus = 1")
    List<Orders> findReservedOrdersBySpaceIdAndDate(@Param("spaceId") String spaceId, @Param("date") java.time.LocalDate date);

    // 【0414薇婷新增】用活動id找舉辦人id
    Optional<Orders> findByEventEventId(String eventId);
    
    
}



