package com.lifespace.dto;

public class RentalItemDetailsDTO {

    private String rentalItemName;
    private Integer rentalItemPrice;
    private Integer rentalItemQuantity;
    private Integer rentalTotalPrice;

    public RentalItemDetailsDTO() {

    }

    public RentalItemDetailsDTO(String rentalItemName, Integer rentalItemPrice, Integer rentalItemQuantity ){
        this.rentalItemName = rentalItemName;
        this.rentalItemPrice = rentalItemPrice;
        this.rentalItemQuantity = rentalItemQuantity;
        this.rentalTotalPrice = rentalItemPrice * rentalItemQuantity;
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

    public Integer getRentalItemQuantity() {
        return rentalItemQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.rentalItemQuantity = rentalItemQuantity;
    }

    public Integer getRentalTotalPrice() {
        return rentalTotalPrice;
    }

    public void setRentalTotalPrice(Integer rentalTotalPrice) {
        this.rentalTotalPrice = rentalTotalPrice;
    }
}
