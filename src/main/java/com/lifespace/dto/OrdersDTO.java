package com.lifespace.dto;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrdersDTO {

    private String orderId;
    private String memberId;
    private String spaceId;
    private String branchId;
    private Timestamp orderStart;
    private Timestamp orderEnd;
    private Integer totalPrice;
    private Timestamp paymentDatetime;
    private Integer orderStatus;
    private String branchAddr;
    private Integer calculatedSpaceFee;
    private String spaceFloor;
    private String spaceCoverPhoto;
    private EventDTO eventDTO;
    private List<RentalItemDetailsDTO> rentalItemDetailsDTOList = new ArrayList<>();

    private Integer accountsPayable;

    public OrdersDTO(){

    }

    public List<RentalItemDetailsDTO> getRentalItemDetailsDTOList() {
        return rentalItemDetailsDTOList;
    }

    public void setRentalItemDetailsDTOList(List<RentalItemDetailsDTO> rentalItemDetailsDTOList) {
        this.rentalItemDetailsDTOList = rentalItemDetailsDTOList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Timestamp getOrderStart() {
        return orderStart;
    }

    public void setOrderStart(Timestamp orderStart) {
        this.orderStart = orderStart;
    }

    public Timestamp getOrderEnd() {
        return orderEnd;
    }

    public void setOrderEnd(Timestamp orderEnd) {
        this.orderEnd = orderEnd;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getPaymentDatetime() {
        return paymentDatetime;
    }

    public void setPaymentDatetime(Timestamp paymentDatetime) {
        this.paymentDatetime = paymentDatetime;
    }

    public EventDTO getEventDTO() {
        return eventDTO;
    }

    public void setEventDTO(EventDTO eventDTO) {
        this.eventDTO = eventDTO;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }


    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchAddr() {
        return branchAddr;
    }

    public void setBranchAddr(String branchAddr) {
        this.branchAddr = branchAddr;
    }

    public Integer getCalculatedSpaceFee() {
        return calculatedSpaceFee;
    }

    public void setCalculatedSpaceFee(Integer calculatedSpaceFee) {
        this.calculatedSpaceFee = calculatedSpaceFee;
    }


    public String getSpaceFloor() {
        return spaceFloor;
    }

    public void setSpaceFloor(String spaceFloor) {
        this.spaceFloor = spaceFloor;
    }

    public String getSpaceLocation() {

        if (branchAddr == null || branchAddr.trim().isEmpty()) {
            return "";
        }

        if (spaceFloor == null || spaceFloor.trim().isEmpty()) {
            return branchAddr;
        }

        return branchAddr + spaceFloor + "樓";

    }
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }


    public Integer getAccountsPayable() {
        return accountsPayable;
    }

    public void setAccountsPayable(Integer accountsPayable) {
        this.accountsPayable = accountsPayable;
    }

    public String getSpaceCoverPhoto() {
        return spaceCoverPhoto;
    }

    public void setSpaceCoverPhoto(String spaceCoverPhoto) {
        this.spaceCoverPhoto = spaceCoverPhoto;
    }
}

