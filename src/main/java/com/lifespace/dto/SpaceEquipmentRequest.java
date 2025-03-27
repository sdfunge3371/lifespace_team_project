package com.lifespace.dto;

public class SpaceEquipmentRequest {
    private Integer spaceEquipId;
    private String spaceEquipName;
    private String spaceEquipComment;


    public Integer getSpaceEquipId() {
        return spaceEquipId;
    }

    public void setSpaceEquipId(Integer spaceEquipId) {
        this.spaceEquipId = spaceEquipId;
    }

    public String getSpaceEquipName() {
        return spaceEquipName;
    }

    public void setSpaceEquipName(String spaceEquipName) {
        this.spaceEquipName = spaceEquipName;
    }

    public String getSpaceEquipComment() {
        return spaceEquipComment;
    }

    public void setSpaceEquipComment(String spaceEquipComment) {
        this.spaceEquipComment = spaceEquipComment;
    }
}
