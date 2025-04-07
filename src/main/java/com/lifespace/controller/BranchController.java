package com.lifespace.controller;

import com.lifespace.dto.BranchDTO;
import com.lifespace.dto.PublicEquipmentDTO;
import com.lifespace.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branch")
public class BranchController {

    @Autowired
    private BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping("/getAll")
    public List<BranchDTO> getAllBranches() {
        return branchService.getAllBranchDTOs();
    }

    @GetMapping("/getByStatus/{status}")
    public List<BranchDTO> getBranchesByStatus(@PathVariable Integer status) {
        return branchService.getBranchesByStatus(status);
    }

    @GetMapping("/getByBranchId/{branchId}")
    public List<BranchDTO> getBranchesByBranchId(@PathVariable String branchId) {
        return branchService.findBranchesByBranchId(branchId);
    }

    @GetMapping("/getByBranchName/{branchName}")
    public List<BranchDTO> getBranchesByBranchName(@PathVariable String branchName) {
        return branchService.findBranchesByBranchName(branchName);
    }

    @PostMapping("/updateStatus/{branchId}")
    public ResponseEntity<String> updateBranchStatus(@PathVariable String branchId) {
        try {
            branchService.updateBranchStatusByBranchId(branchId);
            return ResponseEntity.ok("已成功切換分點狀態: " + branchId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<BranchDTO> addBranch(@RequestBody BranchDTO branchDTO, 
                                              @RequestParam List<PublicEquipmentDTO> publicEquipmentDTOs) {
        try {
            BranchDTO newBranch = branchService.addBranch(branchDTO, publicEquipmentDTOs);
            return ResponseEntity.ok(newBranch);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/update/{branchId}")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable String branchId,
                                                @RequestBody BranchDTO branchDTO,
                                                @RequestParam List<PublicEquipmentDTO> publicEquipmentDTOs) {
        try {
            BranchDTO updatedBranch = branchService.updateBranch(branchId, branchDTO, publicEquipmentDTOs);
            return ResponseEntity.ok(updatedBranch);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/getAllBranchIds")
    public List<String> getAllBranchIds() {
        return branchService.getAllBranchIds();
    }
}