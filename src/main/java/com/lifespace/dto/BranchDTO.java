package com.lifespace.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BranchDTO {
    private String branchId;
    private String branchName;
    private String branchAddr;
    private Double latitude;
    private Double longitude;
    private Integer branchStatus;
    private Timestamp createdTime;
    private List<PublicEquipmentDTO> publicEquipmentDTOList = new ArrayList<>();

    public BranchDTO() {
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchAddr() {
        return branchAddr;
    }

    public void setBranchAddr(String branchAddr) {
        this.branchAddr = branchAddr;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getBranchStatus() {
        return branchStatus;
    }

    public void setBranchStatus(Integer branchStatus) {
        this.branchStatus = branchStatus;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public List<PublicEquipmentDTO> getPublicEquipmentDTOList() {
        return publicEquipmentDTOList;
    }

    public void setPublicEquipmentDTOList(List<PublicEquipmentDTO> publicEquipmentDTOList) {
        this.publicEquipmentDTOList = publicEquipmentDTOList;
    }
}