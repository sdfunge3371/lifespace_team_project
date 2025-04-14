package com.lifespace.dto;

import java.sql.Timestamp;

public class FavoriteSpaceDTO {
    private Integer favoriteSpaceId;
    private String spaceId;
    private String memberId;
    private Timestamp createdTime;
    
    // 空間相關資訊
    private String spaceName;
    private String branchId;
    private String branchName;
    private String branchAddr;
    private Integer spacePeople;
    private Double spaceRating;
    private Integer spaceHourlyFee;
    private String spaceFloor;
    private byte[] spacePhoto;  // 第一張照片

    public FavoriteSpaceDTO() {
    }

    // Getters and Setters
    public Integer getFavoriteSpaceId() {
        return favoriteSpaceId;
    }

    public void setFavoriteSpaceId(Integer favoriteSpaceId) {
        this.favoriteSpaceId = favoriteSpaceId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
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

    public Integer getSpacePeople() {
        return spacePeople;
    }

    public void setSpacePeople(Integer spacePeople) {
        this.spacePeople = spacePeople;
    }

    public Double getSpaceRating() {
        return spaceRating;
    }

    public void setSpaceRating(Double spaceRating) {
        this.spaceRating = spaceRating;
    }

    public Integer getSpaceHourlyFee() {
        return spaceHourlyFee;
    }

    public void setSpaceHourlyFee(Integer spaceHourlyFee) {
        this.spaceHourlyFee = spaceHourlyFee;
    }

    public String getSpaceFloor() {
        return spaceFloor;
    }

    public void setSpaceFloor(String spaceFloor) {
        this.spaceFloor = spaceFloor;
    }

    public byte[] getSpacePhoto() {
        return spacePhoto;
    }

    public void setSpacePhoto(byte[] spacePhoto) {
        this.spacePhoto = spacePhoto;
    }
}