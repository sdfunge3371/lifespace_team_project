package com.lifespace.entity;

import java.sql.Timestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "branch")
public class BranchVO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "branch_id")
    @Pattern(regexp = "^B\\d{3}$", message = "分店編號格式不正確")
    private String branchId;
    
    @Column(name = "branch_name")
    @NotEmpty(message = "分店名稱: 請勿空白")
    private String branchName;
    
    @Column(name = "branch_addr")
    @NotEmpty(message = "分店地址: 請勿空白")
    private String branchAddr;
    
    @Column(name = "space_qty")
    @NotNull(message = "空間數量: 請勿空白")
    private Integer spaceQty;
    
    @Column(name = "latitude")
    @NotNull(message = "緯度: 請勿空白")
    @DecimalMin(value = "-90.0", message = "緯度: 不能小於{value}")
    @DecimalMax(value = "90.0", message = "緯度: 不能超過{value}")
    private Double latitude;
    
    @Column(name = "longitude")
    @NotNull(message = "經度: 請勿空白")
    @DecimalMin(value = "-180.0", message = "經度: 不能小於{value}")
    @DecimalMax(value = "180.0", message = "經度: 不能超過{value}")
    private Double longitude;
    
    @Column(name = "branchstatus")
    private Integer branchStatus;
    
    @Column(name = "created_time", insertable = false, updatable = false)
    private Timestamp createdTime;
    
    // 空構造函數
    public BranchVO() { }
    
    // getter 和 setter 方法
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    
    public String getBranchAddr() { return branchAddr; }
    public void setBranchAddr(String branchAddr) { this.branchAddr = branchAddr; }
    
    public Integer getSpaceQty() { return spaceQty; }
    public void setSpaceQty(Integer spaceQty) { this.spaceQty = spaceQty; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Integer getBranchStatus() { return branchStatus; }
    public void setBranchStatus(Integer branchStatus) { this.branchStatus = branchStatus; }
    
    public Timestamp getCreatedTime() { return createdTime; }
    public void setCreatedTime(Timestamp createdTime) { this.createdTime = createdTime; }
}