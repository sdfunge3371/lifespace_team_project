package com.lifespace.mapper;

import com.lifespace.dto.OrdersDTO;
import com.lifespace.entity.Orders;
import com.lifespace.entity.Event;
import com.lifespace.dto.EventDTO;

public class OrdersMapper {

    public static OrdersDTO toOrdersDTO(Orders orders) {

        OrdersDTO dto = new OrdersDTO();

        dto.setOrderId(orders.getOrderId());
        dto.setSpaceId(orders.getSpaceId());
        dto.setBranchId(orders.getBranchId());
        dto.setTotalPrice(orders.getTotalPrice());
        dto.setOrderStart(orders.getOrderStart());
        dto.setOrderEnd(orders.getOrderEnd());
        dto.setPaymentDatetime(orders.getPaymentDatetime());
        dto.setOrderStatus(orders.getOrderStatus());

        if(orders.getEvent() != null) {
            dto.setEventDTO(toEventDTO(orders.getEvent()));
        }
        return dto;
    }

    public static EventDTO toEventDTO(Event event) {
        return new EventDTO(event.getEventId(), event.getEventName());
    }


}
