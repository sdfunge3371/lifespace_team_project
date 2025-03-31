package com.lifespace.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.lifespace.entity.BranchVO;
import com.lifespace.service.BranchService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/branch")
public class BranchController {

    @Autowired
    private BranchService branchSvc;
    
    /**
     * 查詢頁面
     */
    @GetMapping("/select_page")
    public String select_page(ModelMap model) {
        List<BranchVO> list = branchSvc.getAll();
        model.addAttribute("branchListData", list);
        return "back-end/branch/select_page";
    }
    
    /**
     * 顯示所有分店資料 for Thymeleaf
     */
    @GetMapping("/listAllBranch")
    public String listAllBranch(ModelMap model) {
        List<BranchVO> list = branchSvc.getAll();
        model.addAttribute("branchListData", list);
        return "back-end/branch/listAllBranch";
    }
    /**
     * 顯示所有分店資料 for Ajax
     */
    @GetMapping("/getAllBranches")
    @ResponseBody
    public ResponseEntity<List<BranchVO>> listAllBranch() {
        List<BranchVO> list = branchSvc.getAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }


    /**
     * 顯示新增表單
     */
    @GetMapping("/addBranch")
    public String addBranch(ModelMap model) {
        BranchVO branchVO = new BranchVO();
        model.addAttribute("branchVO", branchVO);
        return "back-end/branch/addBranch";
    }
    
    /**
     * 處理新增請求
     */
    @PostMapping("/insert")
    public String insert(@Valid BranchVO branchVO, BindingResult result, ModelMap model) {
        // 1. 接收請求參數 - 輸入格式的錯誤處理
        if (result.hasErrors()) {
            return "back-end/branch/addBranch";
        }
        
        // 2. 開始新增資料
        branchSvc.addBranch(
            branchVO.getBranchName(), 
            branchVO.getBranchAddr(), 
            branchVO.getSpaceQty(), 
            branchVO.getLatitude(), 
            branchVO.getLongitude(), 
            branchVO.getBranchStatus()
        );
        
        // 3. 新增完成,準備轉交
        List<BranchVO> list = branchSvc.getAll();
        model.addAttribute("branchListData", list);
        model.addAttribute("success", "- (新增成功)");
        return "redirect:/branch/listAllBranch";
    }
    
    /**
     * 顯示單一分店資料
     */
    @PostMapping("/getOne_For_Display")
    public String getOne_For_Display(@RequestParam("branchId") String branchId, ModelMap model) {
        // 1. 接收請求參數 - 輸入格式的錯誤處理
        List<String> errorMsgs = new LinkedList<String>();
        model.addAttribute("errorMsgs", errorMsgs);
        
        if (branchId == null || branchId.trim().length() == 0) {
            errorMsgs.add("分店編號不得為空");
        }
        
        if (!errorMsgs.isEmpty()) {
            return "back-end/branch/select_page";
        }
        
        if (!branchId.trim().matches("^B\\d{3}$")) {
            errorMsgs.add("分店編號格式不正確");
        }
        
        if (!errorMsgs.isEmpty()) {
            return "back-end/branch/select_page";
        }
        
        // 2. 開始查詢資料
        BranchVO branchVO = branchSvc.getOneBranch(branchId);
        if (branchVO == null) {
            errorMsgs.add("查無資料");
        }
        
        if (!errorMsgs.isEmpty()) {
            return "back-end/branch/select_page";
        }
        
        // 3. 查詢完成,準備轉交
        model.addAttribute("branchVO", branchVO);
        return "back-end/branch/listOneBranch";
    }
    
    /**
     * 顯示修改表單
     */
    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("branchId") String branchId, ModelMap model) {
        // 1. 接收請求參數
        // 2. 開始查詢資料
        BranchVO branchVO = branchSvc.getOneBranch(branchId);
        
        // 3. 查詢完成,準備轉交
        model.addAttribute("branchVO", branchVO);
        return "back-end/branch/update_branch_input";
    }
    
    /**
     * 處理修改請求
     */
    @PostMapping("/update")
    public String update(@Valid BranchVO branchVO, BindingResult result, ModelMap model) {
        // 1. 接收請求參數 - 輸入格式的錯誤處理
        if (result.hasErrors()) {
            return "back-end/branch/update_branch_input";
        }
        
        // 2. 開始修改資料
        branchSvc.updateBranch(
            branchVO.getBranchId(),
            branchVO.getBranchName(), 
            branchVO.getBranchAddr(), 
            branchVO.getSpaceQty(), 
            branchVO.getLatitude(), 
            branchVO.getLongitude(), 
            branchVO.getBranchStatus()
        );
        
        // 3. 修改完成,準備轉交
        model.addAttribute("success", "- (修改成功)");
        branchVO = branchSvc.getOneBranch(branchVO.getBranchId());
        model.addAttribute("branchVO", branchVO);
        return "back-end/branch/listOneBranch";
    }
    
    /**
     * 處理刪除請求
     */
    @PostMapping("/delete")
    public String delete(@RequestParam("branchId") String branchId, ModelMap model) {
        // 1. 接收請求參數
        // 2. 開始刪除資料
        branchSvc.deleteBranch(branchId);
        
        // 3. 刪除完成,準備轉交
        List<BranchVO> list = branchSvc.getAll();
        model.addAttribute("branchListData", list);
        model.addAttribute("success", "- (刪除成功)");
        return "back-end/branch/listAllBranch";
    }
}