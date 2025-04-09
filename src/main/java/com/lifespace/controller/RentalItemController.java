package com.lifespace.controller;

import com.lifespace.dto.RentalItemDTO;
import com.lifespace.service.RentalItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rental-item")
public class RentalItemController {

    @Autowired
    private RentalItemService rentalItemService;

    public RentalItemController(RentalItemService rentalItemService) {
        this.rentalItemService = rentalItemService;
    }

    @GetMapping("/getAll")
    public List<RentalItemDTO> getAllRentalItems() {
        return rentalItemService.getAllRentalItemDTOs();
    }

    @GetMapping("/getByStatus/{status}")
    public List<RentalItemDTO> getRentalItemsByStatus(@PathVariable Integer status) {
        return rentalItemService.getRentalItemsByStatus(status);
    }

    @GetMapping("/getByRentalItemId/{rentalItemId}")
    public List<RentalItemDTO> getRentalItemsByRentalItemId(@PathVariable String rentalItemId) {
        return rentalItemService.findRentalItemsByRentalItemId(rentalItemId);
    }

    @GetMapping("/getByRentalItemName/{rentalItemName}")
    public List<RentalItemDTO> getRentalItemsByRentalItemName(@PathVariable String rentalItemName) {
        return rentalItemService.findRentalItemsByRentalItemName(rentalItemName);
    }

    @GetMapping("/getByBranchId/{branchId}")
    public List<RentalItemDTO> getRentalItemsByBranchId(@PathVariable String branchId) {
        return rentalItemService.getRentalItemsByBranchId(branchId);
    }

    @PostMapping("/updateStatus/{rentalItemId}")
    public ResponseEntity<String> updateRentalItemStatus(@PathVariable String rentalItemId) {
        try {
            rentalItemService.updateRentalItemStatusByRentalItemId(rentalItemId);
            return ResponseEntity.ok("已成功切換租借品項狀態: " + rentalItemId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<RentalItemDTO> addRentalItem(@RequestBody RentalItemDTO rentalItemDTO) {
        try {
            RentalItemDTO newRentalItem = rentalItemService.addRentalItem(rentalItemDTO);
            return ResponseEntity.ok(newRentalItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/update/{rentalItemId}")
    public ResponseEntity<RentalItemDTO> updateRentalItem(
            @PathVariable String rentalItemId,
            @RequestBody RentalItemDTO rentalItemDTO) {
        try {
            RentalItemDTO updatedRentalItem = rentalItemService.updateRentalItem(rentalItemId, rentalItemDTO);
            return ResponseEntity.ok(updatedRentalItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
