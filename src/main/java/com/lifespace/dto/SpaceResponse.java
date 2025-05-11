package com.lifespace.dto;

import com.lifespace.entity.Space;
import com.lifespace.entity.SpacePhoto;
import com.lifespace.entity.SpaceUsage;
import com.lifespace.entity.SpaceUsageMap;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpaceResponse {
    private String spaceId;
    private String spaceName;
    private String branchId;
    private String branchAddr;
    private String branchName;
    private Integer spaceHourlyFee;
    private Double spaceRating;
    private Integer spacePeople;
    private Integer spaceStatus;
    private Integer branchStatus;
//    private Set<SpaceUsageMap> spaceUsageMaps;
//    private Set<SpacePhoto> spacePhotos;
    private List<String> spaceUsages;
    private List<byte[]> spacePhotos;

    private Double latitude;
    private Double longitude;

    public SpaceResponse(Space space) {
        this.spaceId = space.getSpaceId();
        this.spaceName = space.getSpaceName();
        this.branchId = space.getBranchId();
        this.branchAddr = space.getBranchAddr();
        this.branchName = space.getBranchName();
        this.spaceHourlyFee = space.getSpaceHourlyFee();
        this.spaceRating = space.getSpaceRating();
        this.spacePeople = space.getSpacePeople();
        this.spaceStatus = space.getSpaceStatus();
        this.branchStatus = space.getBranchStatus();
        this.latitude = space.getLatitude();
        this.longitude = space.getLongitude();
//        this.spaceUsageMaps = space.getSpaceUsageMaps();
//        this.spacePhotos = space.getSpacePhotos();
        this.spaceUsages = space.getSpaceUsageMaps() != null ?
                space.getSpaceUsageMaps().stream()
                        .map(map -> map.getSpaceUsage().getSpaceUsageName())
                        .collect(Collectors.toList())
                : null;
        this.spacePhotos = space.getSpacePhotos() != null ?
                space.getSpacePhotos().stream()
                        .map(map -> map.getPhoto())  // 這邊看你的SpacePhoto的設計
                        .collect(Collectors.toList())
                : null;
    }

    // Getter & Setter

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

    public String getBranchAddr() {
        return branchAddr;
    }

    public void setBranchAddr(String branchAddr) {
        this.branchAddr = branchAddr;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Integer getSpaceHourlyFee() {
        return spaceHourlyFee;
    }

    public void setSpaceHourlyFee(Integer spaceHourlyFee) {
        this.spaceHourlyFee = spaceHourlyFee;
    }

    public Double getSpaceRating() {
        return spaceRating;
    }

    public void setSpaceRating(Double spaceRating) {
        this.spaceRating = spaceRating;
    }

    public Integer getSpacePeople() {
        return spacePeople;
    }

    public void setSpacePeople(Integer spacePeople) {
        this.spacePeople = spacePeople;
    }

    public Integer getSpaceStatus() {
        return spaceStatus;
    }

    public void setSpaceStatus(Integer spaceStatus) {
        this.spaceStatus = spaceStatus;
    }

    public Integer getBranchStatus() {
        return branchStatus;
    }

    public void setBranchStatus(Integer branchStatus) {
        this.branchStatus = branchStatus;
    }

    public List<String> getSpaceUsages() {
        return spaceUsages;
    }

    public void setSpaceUsages(List<String> spaceUsages) {
        this.spaceUsages = spaceUsages;
    }

    public List<byte[]> getSpacePhotos() {
        return spacePhotos;
    }

    public void setSpacePhotos(List<byte[]> spacePhotos) {
        this.spacePhotos = spacePhotos;
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
}

