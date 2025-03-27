package com.lifespace.dto;

import com.lifespace.entity.SpaceEquipment;

import java.util.List;
import java.util.Set;

public class SpaceRequest {
    private String spaceId;
    private String spaceName;
    private String branchId;
    private Integer spacePeople;
    private Double spaceSize;
    private Integer spaceHourlyFee;
    private Integer spaceDailyFee;
    private String spaceDesc;
    private String spaceAlert;
    private Integer spaceStatus;
    private String spaceFloor;

    private Set<SpaceEquipment> spaceEquipments;  // ✔ 傳進來的設備資料（非ID，是一整筆）
    // 備註：照片是用 MultipartFile files 接收，所以不需放在 DTO

    private List<String> spaceUsageIds; // ✔ 來自下拉式多選：用途 ID 清單

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public Integer getSpacePeople() {
        return spacePeople;
    }

    public void setSpacePeople(Integer spacePeople) {
        this.spacePeople = spacePeople;
    }

    public Double getSpaceSize() {
        return spaceSize;
    }

    public void setSpaceSize(Double spaceSize) {
        this.spaceSize = spaceSize;
    }

    public Integer getSpaceHourlyFee() {
        return spaceHourlyFee;
    }

    public void setSpaceHourlyFee(Integer spaceHourlyFee) {
        this.spaceHourlyFee = spaceHourlyFee;
    }

    public Integer getSpaceDailyFee() {
        return spaceDailyFee;
    }

    public void setSpaceDailyFee(Integer spaceDailyFee) {
        this.spaceDailyFee = spaceDailyFee;
    }

    public String getSpaceDesc() {
        return spaceDesc;
    }

    public void setSpaceDesc(String spaceDesc) {
        this.spaceDesc = spaceDesc;
    }

    public String getSpaceAlert() {
        return spaceAlert;
    }

    public void setSpaceAlert(String spaceAlert) {
        this.spaceAlert = spaceAlert;
    }

    public Integer getSpaceStatus() {
        return spaceStatus;
    }

    public void setSpaceStatus(Integer spaceStatus) {
        this.spaceStatus = spaceStatus;
    }

    public String getSpaceFloor() {
        return spaceFloor;
    }

    public void setSpaceFloor(String spaceFloor) {
        this.spaceFloor = spaceFloor;
    }

    public List<String> getSpaceUsageIds() {
        return spaceUsageIds;
    }

    public void setSpaceUsageIds(List<String> spaceUsageIds) {
        this.spaceUsageIds = spaceUsageIds;
    }

    public Set<SpaceEquipment> getSpaceEquipments() {
        return spaceEquipments;
    }

    public void setSpaceEquipments(Set<SpaceEquipment> spaceEquipments) {
        this.spaceEquipments = spaceEquipments;
    }
}
