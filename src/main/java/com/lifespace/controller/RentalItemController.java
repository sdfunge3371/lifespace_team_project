package com.lifespace.controller;

import com.lifespace.entity.RentalItem;
import com.lifespace.service.RentalItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentalItem")
public class RentalItemController {

    @Autowired
    private RentalItemService rentalItemSvc;
}
