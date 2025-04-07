package com.lifespace.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rental_item")
public class RentalItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.lifespace.util.RentalItemCustomStringIdGenerator")
    @Column(name = "rental_item_id")
    private String rentalItemId;

    @Column(name = "rental_item_name", nullable = false, length = 20)
    private String rentalItemName;

    @Column(name = "rental_item_price", nullable = false)
    private Integer rentalItemPrice;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "available_rental_quantity", nullable = false)
    private Integer availableRentalQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "rental_item_status", nullable = false)
    private Integer rentalItemStatus;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @OneToMany(mappedBy = "rentalItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RentalItemDetails> rentalItemDetails = new ArrayList<>();

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

    public List<RentalItemDetails> getRentalItemDetails() {
        return rentalItemDetails;
    }

    public void setRentalItemDetails(List<RentalItemDetails> rentalItemDetails) {
        this.rentalItemDetails = rentalItemDetails;
    }
}