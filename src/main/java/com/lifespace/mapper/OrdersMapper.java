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
        dto.setAccountsPayable(orders.getAccountsPayable());
        dto.setOrderStatus(orders.getOrderStatus());
        dto.setBranchAddr(orders.getBranch().getBranchAddr());
        dto.setSpaceFloor(orders.getSpace().getSpaceFloor());

        if(orders.getEvent() != null) {
            dto.setEventDTO(toEventDTO(orders.getEvent()));
        }

        if(orders.getRentalItemDetails() != null){
            dto.setRentalItemDetailsDTOList(toRentalItemDetailsDTOList(new ArrayList<>(orders.getRentalItemDetails()))
            );
        }

//        if (orders.getBranch() != null && orders.getSpace().getSpaceFloor() != null) {
//            dto.setBranchAddr(orders.getBranch().getBranchAddr());
//            dto.setSpaceFloor(orders.getSpace().getSpaceFloor());
//        }

        if (orders.getOrderStart() != null && orders.getOrderEnd() != null) {
            long spaceMillis = orders.getOrderEnd().getTime() - orders.getOrderStart().getTime();
            long spaceHalfHourUnits = spaceMillis / (1000 * 60 * 30) ; //每30分鐘
            int halfHourPrice = orders.getSpace().getSpaceHourlyFee() / 2;
            int spaceFee =(int) (spaceHalfHourUnits * halfHourPrice);
            dto.setCalculatedSpaceFee(spaceFee);
        }

        return dto;
    }

    public static EventDTO toEventDTO(Event event) {

        EventDTO eventDTO = new EventDTO(event.getEventId(), event.getEventName());

        if (event.getEventCategory() != null) {
            eventDTO.setEventCategoryName(event.getEventCategory().getEventCategoryName());
        }

        return eventDTO;
    }

    public static List<RentalItemDetailsDTO> toRentalItemDetailsDTOList(List<RentalItemDetails> rentalItemDetails) {

        return rentalItemDetails.stream().map(rentalItemDetail -> {
                RentalItem rentalItem = rentalItemDetail.getRentalItem();
                return new RentalItemDetailsDTO(
                        rentalItem.getRentalItemName(),
                        rentalItem.getRentalItemPrice(),
                        rentalItemDetail.getRentalItemQuantity()
                );
        }).collect(Collectors.toList());

    }


}
