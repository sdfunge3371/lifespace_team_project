package com.lifespace.repository;

import com.lifespace.entity.Member;
import com.lifespace.entity.Orders;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
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

    List<Orders> findByOrderStatusAndOrderEndBefore(Integer orderStatus, Timestamp OrderEnd );

    @EntityGraph(attributePaths = {"branch", "member", "rentalItemDetails", "rentalItemDetails.rentalItem"})
    List<Orders> findAll();

    @EntityGraph(attributePaths = {"branch", "member", "rentalItemDetails", "rentalItemDetails.rentalItem", "event"})
    List<Orders> findByMember_MemberId(String memberId);


    @Query("SELECT o FROM Orders o WHERE o.member.memberId = :memberId And o.member.accountStatus = 1")
    List<Orders> findAllByMemberId(@Param("memberId") String memberId);

    //line官方第一次用memberId查三筆已付款訂單
    List<Orders> findTop3ByMemberIdAndOrderStatusOrderByOrderStartDesc(String memberId, Integer orderStatus);

    //line官方第二次之後用lineUserId查三筆已付款訂單
    List<Orders> findTop3ByLineUserIdAndOrderStatusOrderByOrderStartDesc(String lineUserId, Integer orderStatus);

    //判斷訂單是否有存LineUserId
    boolean existsByLineUserId(String lineUserId);

    @Modifying
    @Query("UPDATE Orders o Set o.lineUserId = :userId WHERE o.orderId IN :orderIds AND o.lineUserId IS NULL")
    int bulkInsertLineUserIdIfNull(@Param("userId") String userId, @Param("orderIds") List<String> orderIds);

    //用舉辦人id以及活動id查詢訂單，作為舉辦者取消活動用
    Optional<Orders> findByEventEventIdAndMemberMemberId(String eventId, String memberId);

    // 根據空間抓該空間已經被預訂的所有時段
    @Query("SELECT o FROM Orders o WHERE o.space.spaceId = :spaceId AND FUNCTION('DATE', o.orderStart) = :date AND o.orderStatus = 1")
    List<Orders> findReservedOrdersBySpaceIdAndDate(@Param("spaceId") String spaceId, @Param("date") java.time.LocalDate date);

    // 【0414薇婷新增】用活動id找舉辦人id
    Optional<Orders> findByEventEventId(String eventId);
    
    
}



