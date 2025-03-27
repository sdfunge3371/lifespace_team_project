package com.lifespace.controller;


import com.lifespace.dto.OrdersDTO;
import com.lifespace.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersSvc;

    public OrdersController(OrdersService ordersSvc) {
    this.ordersSvc = ordersSvc;
    }


    @GetMapping("/getAll")
    public List<OrdersDTO> getAllOrders() {

        return ordersSvc.getAllOrdersDTOs();
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable String orderId) {
        ordersSvc.updateOrderStatusByOrderId(orderId); // 改變訂單狀態
        return ResponseEntity.ok("success");
    }

}
