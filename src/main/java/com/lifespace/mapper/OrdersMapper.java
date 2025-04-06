package com.lifespace.mapper;

import com.lifespace.dto.OrdersDTO;
import com.lifespace.dto.RentalItemDetailsDTO;
import com.lifespace.entity.Orders;
import com.lifespace.entity.Event;
import com.lifespace.dto.EventDTO;
import com.lifespace.entity.RentalItem;
import com.lifespace.entity.RentalItemDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrdersMapper {

    public static OrdersDTO toOrdersDTO(Orders orders) {

        OrdersDTO dto = new OrdersDTO();

        dto.setOrderId(orders.getOrderId());
        dto.setMemberId(orders.getMember().getMemberId());
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

        if(orders.getRentalItemDetails() != null){
            dto.setRentalItemDetailsDTOList(toRentalItemDetailsDTOList(new ArrayList<>(orders.getRentalItemDetails()))
            );
        }

        if (orders.getBranchVO() != null) {
            dto.setBranchAddr(orders.getBranchVO().getBranchAddr());
        }
        return dto;
    }

    public static EventDTO toEventDTO(Event event) {

        return new EventDTO(event.getEventId(), event.getEventName());
    }

    public static List<RentalItemDetailsDTO> toRentalItemDetailsDTOList(List<RentalItemDetails> rentalItemDetails) {

        return rentalItemDetails
                .stream()
                .map(rentalItemDetail -> {
                    RentalItem rentalItem = rentalItemDetail.getRentalItem();
                    return new RentalItemDetailsDTO(
                            rentalItem.getRentalItemName(),
                            rentalItem.getRentalItemPrice(),
                            rentalItemDetail.getRentalItemQuantity()
                    );
                }).collect(Collectors.toList());



    }




}
