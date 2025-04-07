package com.lifespace.dto;

import java.sql.Timestamp;

public class PublicEquipmentDTO {
    private Integer publicEquipId;
    private String branchId;
    private String publicEquipName;
    private Timestamp createdTime;

    public PublicEquipmentDTO() {
    }

    public PublicEquipmentDTO(Integer publicEquipId, String publicEquipName) {
        this.publicEquipId = publicEquipId;
        this.publicEquipName = publicEquipName;
    }

    public Integer getPublicEquipId() {
        return publicEquipId;
    }

    public void setPublicEquipId(Integer publicEquipId) {
        this.publicEquipId = publicEquipId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getPublicEquipName() {
        return publicEquipName;
    }

    public void setPublicEquipName(String publicEquipName) {
        this.publicEquipName = publicEquipName;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
}