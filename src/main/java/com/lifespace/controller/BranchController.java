package com.lifespace.controller;

import com.lifespace.dto.BranchDTO;
import com.lifespace.dto.PublicEquipmentDTO;
import com.lifespace.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<BranchDTO> addBranch(@RequestBody Map<String, Object> requestMap) {
        try {
            BranchDTO branchDTO;
            List<PublicEquipmentDTO> publicEquipmentDTOs;
            
            // 檢查是否是完整請求（包含分點和公共設備）或僅分點資料
            if (requestMap.containsKey("branchDTO") && requestMap.containsKey("publicEquipmentDTOs")) {
                branchDTO = convertMapToBranchDTO((Map<String, Object>) requestMap.get("branchDTO"));
                publicEquipmentDTOs = convertToPublicEquipmentDTOList((List<Map<String, Object>>) requestMap.get("publicEquipmentDTOs"));
            } else {
                branchDTO = convertMapToBranchDTO(requestMap);
                publicEquipmentDTOs = List.of(); // 空列表
            }
            
            BranchDTO newBranch = branchService.addBranch(branchDTO, publicEquipmentDTOs);
            return ResponseEntity.ok(newBranch);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/update/{branchId}")
    public ResponseEntity<BranchDTO> updateBranch(
            @PathVariable String branchId,
            @RequestBody Map<String, Object> requestMap) {
        try {
            BranchDTO branchDTO = convertMapToBranchDTO((Map<String, Object>) requestMap.get("branchDTO"));
            List<PublicEquipmentDTO> publicEquipmentDTOs = convertToPublicEquipmentDTOList(
                    (List<Map<String, Object>>) requestMap.get("publicEquipmentDTOs"));
            
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
    
    // 輔助方法：將 Map 轉換為 BranchDTO
    private BranchDTO convertMapToBranchDTO(Map<String, Object> map) {
        BranchDTO dto = new BranchDTO();
        
        if (map.containsKey("branchId") && map.get("branchId") != null) {
            dto.setBranchId((String) map.get("branchId"));
        }
        
        if (map.containsKey("branchName")) {
            dto.setBranchName((String) map.get("branchName"));
        }
        
        if (map.containsKey("branchAddr")) {
            dto.setBranchAddr((String) map.get("branchAddr"));
        }
        
        if (map.containsKey("latitude")) {
            // 處理前端傳來的數字類型
            Object latObj = map.get("latitude");
            if (latObj instanceof Double) {
                dto.setLatitude((Double) latObj);
            } else if (latObj instanceof Integer) {
                dto.setLatitude(((Integer) latObj).doubleValue());
            } else if (latObj instanceof String) {
                dto.setLatitude(Double.parseDouble((String) latObj));
            }
        }
        
        if (map.containsKey("longitude")) {
            // 處理前端傳來的數字類型
            Object lngObj = map.get("longitude");
            if (lngObj instanceof Double) {
                dto.setLongitude((Double) lngObj);
            } else if (lngObj instanceof Integer) {
                dto.setLongitude(((Integer) lngObj).doubleValue());
            } else if (lngObj instanceof String) {
                dto.setLongitude(Double.parseDouble((String) lngObj));
            }
        }
        
        if (map.containsKey("branchStatus")) {
            // 處理前端傳來的數字類型
            Object statusObj = map.get("branchStatus");
            if (statusObj instanceof Integer) {
                dto.setBranchStatus((Integer) statusObj);
            } else if (statusObj instanceof String) {
                dto.setBranchStatus(Integer.parseInt((String) statusObj));
            }
        }
        
        return dto;
    }
    
    // 輔助方法：將 List<Map> 轉換為 List<PublicEquipmentDTO>
    private List<PublicEquipmentDTO> convertToPublicEquipmentDTOList(List<Map<String, Object>> mapList) {
        if (mapList == null) {
            return List.of();
        }
        
        return mapList.stream()
                .map(map -> {
                    PublicEquipmentDTO dto = new PublicEquipmentDTO();
                    
                    if (map.containsKey("publicEquipId") && map.get("publicEquipId") != null) {
                        Object idObj = map.get("publicEquipId");
                        if (idObj instanceof Integer) {
                            dto.setPublicEquipId((Integer) idObj);
                        } else if (idObj instanceof String) {
                            dto.setPublicEquipId(Integer.parseInt((String) idObj));
                        }
                    }
                    
                    if (map.containsKey("publicEquipName")) {
                        dto.setPublicEquipName((String) map.get("publicEquipName"));
                    }
                    
                    if (map.containsKey("branchId")) {
                        dto.setBranchId((String) map.get("branchId"));
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
}