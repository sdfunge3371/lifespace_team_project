package com.lifespace.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.GeneratedValue;

import java.sql.Timestamp;

@Entity
@Table(name = "rental_item")
public class RentalItem implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rental_item_id")
    @Pattern(regexp = "^R\\d{3}$", message = "租借品項流水號格式不正確")
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.lifespace.util.RentalItemCustomStringIdGenerator")
    private String rentalItemId;

    @Column(name = "rental_item_name")
    @NotEmpty(message = "租借品項名稱: 請勿空白")
    private String rentalItemName;

    @Column(name = "rental_item_price")
    @NotNull(message = "租借品項價格: 請勿空白")
    @Min(value = 0, message = "租借品項價格: 不能小於{value}")
    private Integer rentalItemPrice;

    @Column(name = "total_quantity")
    @NotNull(message = "商品總數: 請勿空白")
    @Min(value = 0, message = "商品總數: 不能小於{value}")
    private Integer totalQuantity;

    @Column(name = "available_rental_quantity")
    @NotNull(message = "可租借數量: 請勿空白")
    @Min(value = 0, message = "可租借數量: 不能小於{value}")
    private Integer availableRentalQuantity;

    @Column(name = "pause_rental_quantity")
    @NotNull(message = "暫停租借數量: 請勿空白")
    @Min(value = 0, message = "暫停租借數量: 不能小於{value}")
    private Integer pauseRentalQuantity;

    @Column(name = "branch_id")
    @Pattern(regexp = "^B\\d{3}$", message = "分店流水號格式不正確")
    private String branchId;

    @Column(name = "created_time", insertable = false, updatable = false)
    private Timestamp createdTime;


    public RentalItem() {
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

    public Integer getPauseRentalQuantity() {
        return pauseRentalQuantity;
    }

    public void setPauseRentalQuantity(Integer pauseRentalQuantity) {
        this.pauseRentalQuantity = pauseRentalQuantity;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
}


