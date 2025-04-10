package com.lifespace.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lifespace.util.RentalItemCustomStringIdGenerator;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "rental_item")
public class RentalItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "rental-item-id-generator")
    @GenericGenerator(name = "rental-item-id-generator", 
                     type = RentalItemCustomStringIdGenerator.class)
    @Column(name = "rental_item_id")
    private String rentalItemId;

    @Column(name = "rental_item_name")
    private String rentalItemName;

    @Column(name = "rental_item_price")
    private Integer rentalItemPrice;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "available_rental_quantity")
    private Integer availableRentalQuantity;

    @ManyToOne
    @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", nullable = false)
    @JsonBackReference
    private Branch branch;

    @Column(name = "rental_item_status")
    private Integer rentalItemStatus;

    @Column(name = "created_time")
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

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
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