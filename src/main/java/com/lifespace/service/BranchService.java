package com.lifespace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lifespace.entity.BranchVO;
import com.lifespace.repository.BranchRepository;

@Service("branchService")
public class BranchService {

    @Autowired
    private BranchRepository repository;
    
//     使用原本的 DAO 作為備用
//    private BranchDAO_interface dao;
//    
//    public BranchService() {
//        dao = new BranchJDBCDAO();
//    }
    
    /**
     * 新增分店
     */
    @Transactional
    public BranchVO addBranch(String branchName, String branchAddr, 
                              Integer spaceQty, Double latitude, 
                              Double longitude, Integer branchStatus) {
        
        BranchVO branchVO = new BranchVO();
        branchVO.setBranchName(branchName);
        branchVO.setBranchAddr(branchAddr);
        branchVO.setSpaceQty(spaceQty);
        branchVO.setLatitude(latitude);
        branchVO.setLongitude(longitude);
        branchVO.setBranchStatus(branchStatus);
        
        // 使用原有 DAO 的方法獲取下一個流水號並插入
//        dao.insert(branchVO);
        
        // 從數據庫獲取剛插入的數據並返回
        return repository.findById(branchVO.getBranchId()).orElse(branchVO);
    }
    
    /**
     * 更新分店
     */
    @Transactional
    public BranchVO updateBranch(String branchId, String branchName, 
                                 String branchAddr, Integer spaceQty, 
                                 Double latitude, Double longitude, 
                                 Integer branchStatus) {
        
        BranchVO branchVO = new BranchVO();
        branchVO.setBranchId(branchId);
        branchVO.setBranchName(branchName);
        branchVO.setBranchAddr(branchAddr);
        branchVO.setSpaceQty(spaceQty);
        branchVO.setLatitude(latitude);
        branchVO.setLongitude(longitude);
        branchVO.setBranchStatus(branchStatus);
        
        return repository.save(branchVO);
    }
    
    /**
     * 刪除分店
     */
    @Transactional
    public void deleteBranch(String branchId) {
        if (repository.existsById(branchId)) {
            repository.deleteByBranchId(branchId);
        }
    }
    
    /**
     * 查詢單一分店
     */
    public BranchVO getOneBranch(String branchId) {
        Optional<BranchVO> optional = repository.findById(branchId);
        return optional.orElse(null);
    }
    
    /**
     * 查詢所有分店
     */
    public List<BranchVO> getAll() {
        return repository.findAll();
    }
    
    /**
     * 依照分店名稱查詢
     */
    public List<BranchVO> getByBranchName(String branchName) {
        return repository.findByBranchName(branchName);
    }
    
    /**
     * 依照分店地址查詢
     */
    public List<BranchVO> getByBranchAddr(String branchAddr) {
        return repository.findByBranchAddr(branchAddr);
    }
    
    /**
     * 依照分店狀態查詢
     */
    public List<BranchVO> getByBranchStatus(Integer branchStatus) {
        return repository.findByBranchStatus(branchStatus);
    }
    
    /**
     * 複合查詢
     */
    public List<BranchVO> getByIdOrName(String branchId, String branchName) {
        return repository.findByIdOrName(branchId, branchName);
    }
}