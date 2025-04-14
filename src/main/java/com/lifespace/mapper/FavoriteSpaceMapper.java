package com.lifespace.mapper;

import com.lifespace.dto.FavoriteSpaceDTO;
import com.lifespace.entity.FavoriteSpace;

import java.util.List;
import java.util.stream.Collectors;

public class FavoriteSpaceMapper {

    public static FavoriteSpaceDTO toFavoriteSpaceDTO(FavoriteSpace favoriteSpace) {
        if (favoriteSpace == null) {
            return null;
        }

        FavoriteSpaceDTO dto = new FavoriteSpaceDTO();
        dto.setFavoriteSpaceId(favoriteSpace.getFavoriteSpaceId());
        dto.setSpaceId(favoriteSpace.getSpaceId());
        dto.setMemberId(favoriteSpace.getMemberId());
        dto.setCreatedTime(favoriteSpace.getCreatedTime());

        // 如果Space關聯不為空，填充空間相關資訊
        if (favoriteSpace.getSpace() != null) {
            dto.setSpaceName(favoriteSpace.getSpace().getSpaceName());
            dto.setSpacePeople(favoriteSpace.getSpace().getSpacePeople());
            dto.setSpaceRating(favoriteSpace.getSpace().getSpaceRating());
            dto.setSpaceHourlyFee(favoriteSpace.getSpace().getSpaceHourlyFee());
            dto.setSpaceFloor(favoriteSpace.getSpace().getSpaceFloor());
            
            // 取得分店資訊
            if (favoriteSpace.getSpace().getBranch() != null) {
                dto.setBranchId(favoriteSpace.getSpace().getBranch().getBranchId());
                dto.setBranchName(favoriteSpace.getSpace().getBranch().getBranchName());
                dto.setBranchAddr(favoriteSpace.getSpace().getBranch().getBranchAddr());
            }
            
            // 取得第一張照片
            if (favoriteSpace.getSpace().getSpacePhotos() != null && 
                !favoriteSpace.getSpace().getSpacePhotos().isEmpty()) {
                dto.setSpacePhoto(favoriteSpace.getSpace().getSpacePhotos()
                    .stream().findFirst().get().getPhoto());
            }
        }

        return dto;
    }

    public static List<FavoriteSpaceDTO> toFavoriteSpaceDTOList(List<FavoriteSpace> favoriteSpaces) {
        return favoriteSpaces.stream()
                .map(FavoriteSpaceMapper::toFavoriteSpaceDTO)
                .collect(Collectors.toList());
    }
}