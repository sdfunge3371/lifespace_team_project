package com.lifespace.service;

import com.lifespace.dto.RentalItemDTO;
import com.lifespace.entity.Branch;
import com.lifespace.entity.RentalItem;
import com.lifespace.mapper.RentalItemMapper;
import com.lifespace.repository.BranchRepository;
import com.lifespace.repository.RentalItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("rentalItemService")
public class RentalItemService {

    @Autowired
    private RentalItemRepository rentalItemRepository;

    @Autowired
    private BranchRepository branchRepository;

    public void updateRentalItemStatusByRentalItemId(String rentalItemId) {
        RentalItem rentalItem = rentalItemRepository.findById(rentalItemId)
                .orElseThrow(() -> new IllegalArgumentException("租借品項編號" + rentalItemId + "不存在"));

        // 切換狀態（1 -> 0 或 0 -> 1）
        Integer newStatus = (rentalItem.getRentalItemStatus() == 1) ? 0 : 1;
        rentalItem.setRentalItemStatus(newStatus);
        rentalItemRepository.save(rentalItem);
    }

    public RentalItem getOneRentalItem(String rentalItemId) {
        Optional<RentalItem> optional = rentalItemRepository.findById(rentalItemId);
        return optional.orElse(null);
    }

    public List<RentalItem> getAllRentalItems() {
        return rentalItemRepository.findAll();
    }

    public List<RentalItemDTO> getAllRentalItemDTOs() {
        return rentalItemRepository.findAll()
                .stream()
                .map(RentalItemMapper::toRentalItemDTO)
                .collect(Collectors.toList());
    }

    public List<RentalItemDTO> getRentalItemsByStatus(Integer status) {
        return rentalItemRepository.findByRentalItemStatus(status)
                .stream()
                .map(RentalItemMapper::toRentalItemDTO)
                .collect(Collectors.toList());
    }

    public List<RentalItemDTO> findRentalItemsByRentalItemId(String rentalItemId) {
        return rentalItemRepository.findByRentalItemIdContaining(rentalItemId)
                .stream()
                .map(RentalItemMapper::toRentalItemDTO)
                .collect(Collectors.toList());
    }

    public List<RentalItemDTO> findRentalItemsByRentalItemName(String rentalItemName) {
        return rentalItemRepository.findByRentalItemNameContaining(rentalItemName)
                .stream()
                .map(RentalItemMapper::toRentalItemDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalItemDTO addRentalItem(RentalItemDTO rentalItemDTO) {
        // 查找對應的分點
        Branch branch = branchRepository.findById(rentalItemDTO.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("分點編號" + rentalItemDTO.getBranchId() + "不存在"));

        // 創建並設置RentalItem實體
        RentalItem rentalItem = new RentalItem();
        rentalItem.setRentalItemName(rentalItemDTO.getRentalItemName());
        rentalItem.setRentalItemPrice(rentalItemDTO.getRentalItemPrice());
        rentalItem.setTotalQuantity(rentalItemDTO.getTotalQuantity());
        rentalItem.setAvailableRentalQuantity(rentalItemDTO.getAvailableRentalQuantity());
        rentalItem.setBranch(branch);
        rentalItem.setRentalItemStatus(rentalItemDTO.getRentalItemStatus());
        rentalItem.setCreatedTime(Timestamp.from(Instant.now()));

        // 保存RentalItem實體獲取生成的ID
        RentalItem savedRentalItem = rentalItemRepository.save(rentalItem);

        // 轉換為DTO並返回
        return RentalItemMapper.toRentalItemDTO(savedRentalItem);
    }

    @Transactional
    public RentalItemDTO updateRentalItem(String rentalItemId, RentalItemDTO rentalItemDTO) {
        // 獲取現有的RentalItem實體
        RentalItem rentalItem = rentalItemRepository.findById(rentalItemId)
                .orElseThrow(() -> new IllegalArgumentException("租借品項編號" + rentalItemId + "不存在"));

        // 查找對應的分點
        Branch branch = branchRepository.findById(rentalItemDTO.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("分點編號" + rentalItemDTO.getBranchId() + "不存在"));

        // 更新RentalItem屬性
        rentalItem.setRentalItemName(rentalItemDTO.getRentalItemName());
        rentalItem.setRentalItemPrice(rentalItemDTO.getRentalItemPrice());
        rentalItem.setTotalQuantity(rentalItemDTO.getTotalQuantity());
        rentalItem.setAvailableRentalQuantity(rentalItemDTO.getAvailableRentalQuantity());
        rentalItem.setBranch(branch);
        rentalItem.setRentalItemStatus(rentalItemDTO.getRentalItemStatus());
        rentalItem.setCreatedTime(Timestamp.from(Instant.now()));

        // 保存更新後的RentalItem
        RentalItem updatedRentalItem = rentalItemRepository.save(rentalItem);

        // 轉換為DTO並返回
        return RentalItemMapper.toRentalItemDTO(updatedRentalItem);
    }

    public List<RentalItemDTO> getRentalItemsByBranchId(String branchId) {
        return rentalItemRepository.findByBranch_BranchId(branchId)
                .stream()
                .map(RentalItemMapper::toRentalItemDTO)
                .collect(Collectors.toList());
    }
}
