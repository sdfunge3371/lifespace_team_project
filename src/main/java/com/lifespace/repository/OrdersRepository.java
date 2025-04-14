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

    List<Orders> findByOrderStatusAndOrderEndBefore(Integer orderStatus, Timestamp OrderEnd );

    @EntityGraph(attributePaths = {"branch", "member", "rentalItemDetails", "rentalItemDetails.rentalItem"})
    List<Orders> findAll();

    @EntityGraph(attributePaths = {"branch", "member", "rentalItemDetails", "rentalItemDetails.rentalItem", "event"})
    List<Orders> findByMember_MemberIdAndMember_AccountStatus(String memberId, Integer accountStatus);





    //用舉辦人id以及活動id查詢訂單，作為舉辦者取消活動用
    Optional<Orders> findByEventEventIdAndMemberMemberId(String eventId, String memberId);

    // 根據空間抓該空間已經被預訂的所有時段
    @Query("SELECT o FROM Orders o WHERE o.space.spaceId = :spaceId AND FUNCTION('DATE', o.orderStart) = :date AND o.orderStatus = 1")
    List<Orders> findReservedOrdersBySpaceIdAndDate(@Param("spaceId") String spaceId, @Param("date") java.time.LocalDate date);

    // 【0414薇婷新增】用活動id找舉辦人id
    Optional<Orders> findByEventEventId(String eventId);
    
    
}



