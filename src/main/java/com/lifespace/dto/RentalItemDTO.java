package com.lifespace.dto;

import java.sql.Timestamp;

public class RentalItemDTO {
    private String rentalItemId;
    private String rentalItemName;
    private Integer rentalItemPrice;
    private Integer totalQuantity;
    private Integer availableRentalQuantity;
    private String branchId;
    private String branchName; // 額外添加分點名稱，便於顯示
    private Integer rentalItemStatus;
    private Timestamp createdTime;

    public RentalItemDTO() {
    }

    public String getRentalItemId() {
        return rentalItemId;
    }

    public void setRentalItemId(String rentalItemId) {
        this.rentalItemId = rentalItemId;
    }

    public String getRentalItemName() {
        return rentalItemName;
    }

    public void setRentalItemName(String rentalItemName) {
        this.rentalItemName = rentalItemName;
    }

    public Integer getRentalItemPrice() {
        return rentalItemPrice;
    }

    public void setRentalItemPrice(Integer rentalItemPrice) {
        this.rentalItemPrice = rentalItemPrice;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getAvailableRentalQuantity() {
        return availableRentalQuantity;
    }

    public void setAvailableRentalQuantity(Integer availableRentalQuantity) {
        this.availableRentalQuantity = availableRentalQuantity;
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

    public Integer getRentalItemStatus() {
        return rentalItemStatus;
    }

    public void setRentalItemStatus(Integer rentalItemStatus) {
        this.rentalItemStatus = rentalItemStatus;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
}