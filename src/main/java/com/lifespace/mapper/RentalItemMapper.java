package com.lifespace.mapper;

import com.lifespace.dto.RentalItemDTO;
import com.lifespace.entity.RentalItem;

import java.util.List;
import java.util.stream.Collectors;

public class RentalItemMapper {

    public static RentalItemDTO toRentalItemDTO(RentalItem rentalItem) {
        RentalItemDTO dto = new RentalItemDTO();

        dto.setRentalItemId(rentalItem.getRentalItemId());
        dto.setRentalItemName(rentalItem.getRentalItemName());
        dto.setRentalItemPrice(rentalItem.getRentalItemPrice());
        dto.setTotalQuantity(rentalItem.getTotalQuantity());
        dto.setAvailableRentalQuantity(rentalItem.getAvailableRentalQuantity());
        dto.setRentalItemStatus(rentalItem.getRentalItemStatus());
        dto.setCreatedTime(rentalItem.getCreatedTime());

        if (rentalItem.getBranch() != null) {
            dto.setBranchId(rentalItem.getBranch().getBranchId());
            dto.setBranchName(rentalItem.getBranch().getBranchName());
        }

        return dto;
    }

    public static List<RentalItemDTO> toRentalItemDTOList(List<RentalItem> rentalItems) {
        return rentalItems.stream()
                .map(RentalItemMapper::toRentalItemDTO)
                .collect(Collectors.toList());
    }
}