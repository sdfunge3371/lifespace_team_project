package com.lifespace.service;

import com.lifespace.dto.BranchDTO;
import com.lifespace.dto.PublicEquipmentDTO;
import com.lifespace.entity.Branch;
import com.lifespace.entity.PublicEquipment;
import com.lifespace.mapper.BranchMapper;
import com.lifespace.repository.BranchRepository;
import com.lifespace.repository.PublicEquipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("branchService")
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private PublicEquipmentRepository publicEquipmentRepository;

    public void updateBranchStatusByBranchId(String branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("分點編號" + branchId + "不存在"));

        // 切換狀態（1 -> 0 或 0 -> 1）
        Integer newStatus = (branch.getBranchStatus() == 1) ? 0 : 1;
        branch.setBranchStatus(newStatus);
        branchRepository.save(branch);
    }

    public Branch getOneBranch(String branchId) {
        Optional<Branch> optional = branchRepository.findById(branchId);
        return optional.orElse(null);
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public List<BranchDTO> getAllBranchDTOs() {
        return branchRepository.findAll()
                .stream()
                .map(BranchMapper::toBranchDTO)
                .collect(Collectors.toList());
    }

    public List<BranchDTO> getBranchesByStatus(Integer status) {
        return branchRepository.findByBranchStatus(status)
                .stream()
                .map(BranchMapper::toBranchDTO)
                .collect(Collectors.toList());
    }

    public List<BranchDTO> findBranchesByBranchId(String branchId) {
        return branchRepository.findByBranchIdContaining(branchId)
                .stream()
                .map(BranchMapper::toBranchDTO)
                .collect(Collectors.toList());
    }

    public List<BranchDTO> findBranchesByBranchName(String branchName) {
        return branchRepository.findByBranchNameContaining(branchName)
                .stream()
                .map(BranchMapper::toBranchDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BranchDTO addBranch(BranchDTO branchDTO, List<PublicEquipmentDTO> publicEquipmentDTOs) {
        // 創建並設置Branch實體
        Branch branch = new Branch();
        branch.setBranchName(branchDTO.getBranchName());
        branch.setBranchAddr(branchDTO.getBranchAddr());
        branch.setLatitude(branchDTO.getLatitude());
        branch.setLongitude(branchDTO.getLongitude());
        branch.setBranchStatus(branchDTO.getBranchStatus());
        branch.setCreatedTime(Timestamp.from(Instant.now()));

        // 保存Branch實體獲取生成的ID
        Branch savedBranch = branchRepository.save(branch);

        // 創建並保存PublicEquipment實體
        for (PublicEquipmentDTO equipmentDTO : publicEquipmentDTOs) {
            PublicEquipment equipment = new PublicEquipment();
            equipment.setBranch(savedBranch);
            equipment.setPublicEquipName(equipmentDTO.getPublicEquipName());
            equipment.setCreatedTime(Timestamp.from(Instant.now()));
            publicEquipmentRepository.save(equipment);
        }

        // 重新獲取完整的Branch以確保包含所有關聯數據
        Branch refreshedBranch = branchRepository.findById(savedBranch.getBranchId()).orElse(savedBranch);
        return BranchMapper.toBranchDTO(refreshedBranch);
    }

    @Transactional
    public BranchDTO updateBranch(String branchId, BranchDTO branchDTO, List<PublicEquipmentDTO> publicEquipmentDTOs) {
        // 獲取現有的Branch實體
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("分點編號" + branchId + "不存在"));

        // 更新Branch屬性
        branch.setBranchName(branchDTO.getBranchName());
        branch.setBranchAddr(branchDTO.getBranchAddr());
        branch.setLatitude(branchDTO.getLatitude());
        branch.setLongitude(branchDTO.getLongitude());
        branch.setBranchStatus(branchDTO.getBranchStatus());
        branch.setCreatedTime(Timestamp.from(Instant.now()));

        // 保存更新後的Branch
        Branch updatedBranch = branchRepository.save(branch);

        // 刪除原有的公共設備
        publicEquipmentRepository.deleteByBranchId(branchId);

        // 添加新的公共設備
        for (PublicEquipmentDTO equipmentDTO : publicEquipmentDTOs) {
            PublicEquipment equipment = new PublicEquipment();
            equipment.setBranch(updatedBranch);
            equipment.setPublicEquipName(equipmentDTO.getPublicEquipName());
            equipment.setCreatedTime(Timestamp.from(Instant.now()));
            publicEquipmentRepository.save(equipment);
        }

        // 重新獲取完整的Branch以確保包含所有新數據
        Branch refreshedBranch = branchRepository.findById(updatedBranch.getBranchId()).orElse(updatedBranch);
        return BranchMapper.toBranchDTO(refreshedBranch);
    }

    public List<String> getAllBranchIds() {
        return branchRepository.findAll().stream()
                .map(Branch::getBranchId)
                .collect(Collectors.toList());
    }
}