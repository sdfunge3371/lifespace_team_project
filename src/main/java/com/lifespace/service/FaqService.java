package com.lifespace.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.lifespace.dto.AddFaqDTO;
import com.lifespace.dto.FaqDTO;
import com.lifespace.dto.UpdateFaqDTO;
import com.lifespace.entity.FaqVO;
import com.lifespace.repository.FaqRepository;

@Service
public class FaqService {

	@Autowired
	private FaqRepository repository;

	// 自動產生下一個 FAQID
//	private String generateNextFaqId() {
//		// 找出第一筆資料的FAQID存入變數lastId
//		String lastId = repository.findTopByOrderByFaqIdDesc(); 
//
//		int nextNumber = 1; // 如果沒有資料，從 FAQ01 開始
//
//		if (lastId != null && lastId.matches("FAQ\\d+")) {
//			nextNumber = Integer.parseInt(lastId.substring(3)) + 1;
//		}
//
//		return String.format("FAQ%02d", nextNumber); // 轉成 FAQ01、FAQ02 ...
//	}
	// 新增FAQ
	public void insertFaq(String faqAsk, String faqAnswer, String adminId) {
		FaqVO faq = new FaqVO(); // 建立新的實體物件（Entity）
//		faq.setFaqId(generateNextFaqId());
		faq.setFaqAsk(faqAsk);
		faq.setFaqAnswer(faqAnswer);
		faq.setAdminId(adminId);
		faq.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
		faq.setFaqStatus(1);
		repository.save(faq);

	}

	//新增的驗證訊息
	//BindingResult裝著所有欄位的錯誤訊息，如果有錯就可以讀出來回傳給前端
	public ResponseEntity<?> addFaqError(AddFaqDTO dto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			
			// 把錯誤欄位與錯誤訊息逐一存進 map
			bindingResult.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), error.getDefaultMessage());
			});

			return ResponseEntity.badRequest().body(errors);// 回傳錯誤 map (400 Bad Request)
		} else {
			// 沒錯誤才真的寫進資料庫
			insertFaq(dto.getFaqAsk(),dto.getFaqAnswer(), dto.getAdminId());
			
			return ResponseEntity.ok(Map.of("msg", "新增成功")); // 回傳200 OK，body並顯示新增成功
		}
	}

	// 修改FAQ
	public void updateFaq(String faqId, String faqAsk, String faqAnswer) {
		// Optional防止NullPointerException
		Optional<FaqVO> voTemp = repository.findById(faqId);
		if (voTemp.isPresent()) {
			FaqVO vo = voTemp.get();
			vo.setFaqAsk(faqAsk);
			vo.setFaqAnswer(faqAnswer);
			repository.save(vo);
		}
	}
	
//	修改的驗證訊息
	public ResponseEntity<?> updateFaqError(UpdateFaqDTO dto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();

			bindingResult.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), error.getDefaultMessage());
			});

			return ResponseEntity.badRequest().body(errors);
		} else {
			updateFaq(dto.getFaqId(),dto.getFaqAsk(),dto.getFaqAnswer());
			
			return ResponseEntity.ok().build();
		}

	}
	
	// 下架
	public void deprecatedFaq(String faqId) {
		Optional<FaqVO> voTemp = repository.findById(faqId);
		if (voTemp.isPresent()) {	//如果voTemp 有值，代表找到了這筆 FAQ
			FaqVO vo = voTemp.get();
			vo.setFaqStatus(0); // 將狀態改為0
			repository.save(vo);	//修改後的 FAQ 寫回資料庫
		}
	}

	public FaqVO getOneFaq(String faqId) {
		Optional<FaqVO> optional = repository.findById(faqId);
//		return optional.get();
		return optional.orElse(null); // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	// 取得所有欄位
	public List<FaqDTO> getAll() {
		// 建立要回傳給前端的DTO List
		List<FaqDTO> list = new ArrayList<>();	
		// 查詢資料庫中所有FAQ資料（VO）
		List<FaqVO> list2 = repository.findAll();	
		for (FaqVO vo : list2) {
			// 把每一筆VO轉換成DTO，加進list
			list.add(voToFaqDTO(vo));	
		}
		return list;	// 回傳DTO的List給Controller（最後送給前端）
	}

	// 將DTO 轉換成 VO 存入資料庫
	private FaqVO dtoToFaqVo(FaqDTO dto) {
		FaqVO faqVo = new FaqVO();
		faqVo.setFaqId(dto.getFaqId());
		faqVo.setAdminId(dto.getAdminId());
		faqVo.setFaqAsk(dto.getFaqAsk());
		faqVo.setFaqAnswer(dto.getFaqAnswer());
		faqVo.setFaqStatus(dto.getFaqStatus());
		faqVo.setCreateTime(dto.getCreateTime());
		return faqVo;
		
	}
	// 資料回傳前端VO轉換成DTO
	private FaqDTO voToFaqDTO(FaqVO vo) {
		FaqDTO faqDto = new FaqDTO();
		faqDto.setFaqId(vo.getFaqId());
		faqDto.setAdminId(vo.getAdminId());
		faqDto.setFaqAsk(vo.getFaqAsk());
		faqDto.setFaqAnswer(vo.getFaqAnswer());
		faqDto.setFaqStatus(vo.getFaqStatus());
		faqDto.setCreateTime(vo.getCreateTime());
		return faqDto;
	}

}
