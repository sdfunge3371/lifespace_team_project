package com.lifespace.repository;

import com.lifespace.entity.Orders;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;


public interface OrdersRepository extends JpaRepository<Orders, String> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET order_status = ?1 WHERE order_id = ?2", nativeQuery = true)
    void updateOrderStatusByOrderId(Integer orderStatus, String orderId);

    List<Orders> findByOrderStatusAndOrderEndBefore(Integer orderStatus, Timestamp OrderEnd );

    @EntityGraph(attributePaths = {"rentalItemDetails", "rentalItemDetails.rentalItem", "event"})
    List<Orders> findAll();

    @EntityGraph(attributePaths = {"rentalItemDetails", "rentalItemDetails.rentalItem", "event"})
    List<Orders> findByMember_MemberIdAndMember_AccountStatus(String memberId, Integer accountStatus);
}



