package com.lifespace.mapper;

import com.lifespace.dto.BranchDTO;
import com.lifespace.dto.PublicEquipmentDTO;
import com.lifespace.entity.Branch;
import com.lifespace.entity.PublicEquipment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BranchMapper {

    public static BranchDTO toBranchDTO(Branch branch) {
        BranchDTO dto = new BranchDTO();

        dto.setBranchId(branch.getBranchId());
        dto.setBranchName(branch.getBranchName());
        dto.setBranchAddr(branch.getBranchAddr());
        dto.setLatitude(branch.getLatitude());
        dto.setLongitude(branch.getLongitude());
        dto.setBranchStatus(branch.getBranchStatus());
        dto.setCreatedTime(branch.getCreatedTime());

        if (branch.getPublicEquipments() != null) {
            dto.setPublicEquipmentDTOList(toPublicEquipmentDTOList(new ArrayList<>(branch.getPublicEquipments())));
        }

        return dto;
    }

    public static List<PublicEquipmentDTO> toPublicEquipmentDTOList(List<PublicEquipment> publicEquipments) {
        return publicEquipments
                .stream()
                .map(publicEquipment -> {
                    PublicEquipmentDTO dto = new PublicEquipmentDTO(
                        publicEquipment.getPublicEquipId(),
                        publicEquipment.getPublicEquipName()
                    );
                    dto.setBranchId(publicEquipment.getBranch() != null ? 
                                    publicEquipment.getBranch().getBranchId() : null);
                    dto.setCreatedTime(publicEquipment.getCreatedTime());
                    return dto;
                }).collect(Collectors.toList());
    }
}