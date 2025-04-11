package com.lifespace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "rental_item_details")
public class RentalItemDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rental_item_details_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rentalItemDetailsId;

    @Column(name = "rental_item_quantity")
    @Min(value = 1, message = "租借品項數量: 不能小於{value}")
    private Integer rentalItemQuantity;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @ManyToOne
    @JoinColumn(name = "rental_item_id", nullable = false)
    private RentalItem rentalItem;

    public RentalItemDetails() {

    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public RentalItem getRentalItem() {
        return rentalItem;
    }

    public void setRentalItem(RentalItem rentalItem) {
        this.rentalItem = rentalItem;
    }

    public Integer getRentalItemDetailsId() {
        return rentalItemDetailsId;
    }

    public void setRentalItemDetailsId(Integer rentalItemDetailsId) {
        this.rentalItemDetailsId = rentalItemDetailsId;
    }

    public Integer getRentalItemQuantity() { return rentalItemQuantity; }
    public void setRentalItemQuantity(Integer rentalItemQuantity) {
        this.rentalItemQuantity = rentalItemQuantity;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }



}
