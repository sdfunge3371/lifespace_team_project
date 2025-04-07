package com.lifespace.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.lifespace.dto.AddNewsDTO;
import com.lifespace.dto.NewsDTO;
import com.lifespace.dto.NewsUpdateDTO;
import com.lifespace.entity.NewsCategory;
import com.lifespace.entity.NewsStatus;
import com.lifespace.entity.News;
import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.repository.NewsRepository;

import jakarta.annotation.PostConstruct;

@Service
public class NewsService {

	@Autowired
	private NewsRepository repository;

	@Autowired
	private NewsStatusService statusService;
	
	// 狀態常數(給排程器用，可讀性高)
	private static final int STATUS_EXPIRED = 0; //過期
	private static final int STATUS_ACTIVE = 1;  // 上架中
	private static final int STATUS_PENDING = 2; // 即將上架
	

	// -----------從DB撈出所有資料轉成NewsDTO傳給前端(取得所有欄位)--------------
	public List<NewsDTO> getAll() {
		List<NewsDTO> listDto = new ArrayList<>();
		List<News> listVo = repository.findAll();
		for (News vo : listVo) {
			listDto.add(voToNewsDTO(vo));
		}
		return listDto;
	}
	
	
	// 前台取得欄位
		public List<NewsDTO> getAllNews(Integer newsStatusId){
			List<NewsDTO> list = new ArrayList<>();
			List<News> listVO = repository.findByStatus(1);
			for (News vo : listVO) {
				list.add(voToNewsDTO(vo));
			}
			return list;
		}
		
	// ---------------依狀態、分類去篩選條件(最新消息首頁)-------------------
	public List<NewsDTO> getReqColumn(String categoryId, Integer statusId) {
		List<News> list;

		boolean allCategory = categoryId == null || "all".equalsIgnoreCase(categoryId);
		boolean allStatus = statusId == null || statusId == -1; // null:沒選擇； -1:選全部
		// 自訂repository方法
		if (!allCategory && !allStatus) {
			list = repository.findReqColumn(categoryId, statusId);
		} else if (!allCategory) {
			list = repository.findByCategory(categoryId);
		} else if (!allStatus) {
			list = repository.findByStatus(statusId);
		} else {
			list = repository.findAll();
		}
		// List<NewsVO>透過voToNewsDTO方法轉成List<NewsDTO>
		List<NewsDTO> dtoList = new ArrayList<>();
		for (News vo : list) {
			dtoList.add(voToNewsDTO(vo));
		}
		return dtoList;

		// Stream 流處理語法， this::voToNewsDT方法參考（簡寫，省略 Lambda）
//	    return list.stream().map(this::voToNewsDTO).toList();

		// vo -> this.voToNewsDTO(vo)，Lambda 表達式（自己取參數）
//	    return list.stream()
//	            .map(vo -> this.voToNewsDTO(vo)) 
//	            .toList();
	}

	// -------------------新增的驗證訊息-------------------
	public ResponseEntity<?> addNewsError(AddNewsDTO addNewsParam, BindingResult bindingResult, byte[] newsImgBytes) {
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();

			// 把錯誤欄位與錯誤訊息逐一存進 map
			bindingResult.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), error.getDefaultMessage());
			});

			return ResponseEntity.badRequest().body(errors);// 回傳錯誤 map (400 Bad Request)
		} else {
			// 沒錯誤才真的寫進資料庫
			insertNews(addNewsParam, newsImgBytes);

			return ResponseEntity.ok(Map.of("redirect", "/backend_news.html")); // 導回首頁
		}
	}

	// 新增NEWS進資料庫
	public void insertNews(AddNewsDTO addNewsParam, byte[] newsImgBytes) {
		News news = new News(); // 建立新的實體物件（Entity）
		news.setNewsTitle(addNewsParam.getNewsTitle());
		news.setNewsContent(addNewsParam.getNewsContent());
		// LocalDateTime → Timestamp
		news.setNewsStartDate(Timestamp.valueOf(addNewsParam.getNewsStartDate()));
		news.setNewsEndDate(Timestamp.valueOf(addNewsParam.getNewsEndDate()));
		news.setNewsImg(newsImgBytes); // 圖片手動設入
		news.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));

		// 分類對應
		NewsCategory categoryVO = new NewsCategory();
		categoryVO.setNewsCategoryId(addNewsParam.getNewsCategoryId());
		news.setNewsCategory(categoryVO);

		// 狀態對應
		NewsStatus statusVO = new NewsStatus();
		statusVO.setNewsStatusId(addNewsParam.getNewsStatusId());
		news.setNewsStatus(statusVO);

		// 串接管理者
		news.setAdminId("A001");
		repository.save(news);

	}

	// -------------------修改時依ID抓整個欄位-------------------
	public NewsDTO findById(String newsId) {
		NewsDTO dto = new NewsDTO();
		Optional<News> voTmp = repository.findById(newsId);
		if (voTmp.isPresent()) {
			News vo = voTmp.get();
			dto = voToNewsDTO(vo);
		}
		return dto;
	}

	public ResponseEntity<?> updateNewsError(NewsUpdateDTO updateNewsParam, BindingResult bindingResult,
			byte[] newsImgBytes) {
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();

			// 把錯誤欄位與錯誤訊息逐一存進 map
			bindingResult.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), error.getDefaultMessage());
			});

			return ResponseEntity.badRequest().body(errors);// 回傳錯誤 map (400 Bad Request)
		} else {
			// 沒錯誤才真的寫進資料庫
			updateNews(updateNewsParam, newsImgBytes);

			return ResponseEntity.ok(Map.of("redirect", "/backend_news.html")); // 導回首頁
		}
	}

	// 修改NEWS進資料庫
	public void updateNews(NewsUpdateDTO dto, byte[] newsImgBytes) {
		// Optional防止NullPointerException
		Optional<News> voTemp = repository.findById(dto.getNewsId());
		if (voTemp.isPresent()) {
			News vo = voTemp.get();
			vo.setNewsTitle(dto.getNewsTitle());
			vo.setNewsContent(dto.getNewsContent());
			// LocalDateTime → Timestamp
			vo.setNewsStartDate(Timestamp.valueOf(dto.getNewsStartDate()));
			vo.setNewsEndDate(Timestamp.valueOf(dto.getNewsEndDate()));

			if (newsImgBytes != null) {
				vo.setNewsImg(newsImgBytes); // 圖片手動設入
			}

			// 分類對應
			NewsCategory categoryVO = new NewsCategory();
			categoryVO.setNewsCategoryId(dto.getNewsCategoryId());
			vo.setNewsCategory(categoryVO);
			vo.getNewsCategory().setNewsCategoryId(dto.getNewsCategoryId());

			// 狀態對應
			NewsStatus statusVO = new NewsStatus();
			statusVO.setNewsStatusId(dto.getNewsStatusId());
			vo.setNewsStatus(statusVO);

			// 串接管理者
			repository.save(vo);
		}
	}

	// -------------下架---------------
	public void deprecatedNews(String newsId) {
		Optional<News> voTemp = repository.findById(newsId);
		// 如果voTemp 有值，代表找到了這筆News
		if (voTemp.isPresent()) {
			News vo = voTemp.get();
			// 取得過期狀態的物件
			NewsStatus voStatus = statusService.getNewsStatus(0);
			if (voStatus == null) {
				throw new ResourceNotFoundException("查無狀態");
			}
			vo.setNewsStatus(voStatus);
			repository.save(vo);// 修改成過期狀態
		}

	}
	
	// ---------------排程任務：更新狀態---------------
	public void updateNewsStatusAutomatically() {
		//Instant.now()取得現在這一瞬間的「時間點」，回傳Instant物件
		//Timestamp.from(...)Instant 轉換成 java.sql.Timestamp
		Timestamp now = Timestamp.from(Instant.now());
		
		// 即將上架 → 上架中
		List<News> toBeActived = repository.findByNewsStatus_NewsStatusIdAndNewsStartDateBefore(STATUS_PENDING, now);
		// 遍歷每筆news
		for (News news : toBeActived) {
			System.out.println("即將上架改為上架中，ID: " + news.getNewsId() + "，標題: " + news.getNewsTitle());
			// 設定狀態為上架
			news.setNewsStatus(new NewsStatus(STATUS_ACTIVE));
		}
		
	     // 上架中 → 過期
        List<News> toBeExpired = repository.findByNewsStatus_NewsStatusIdAndNewsEndDateBefore(STATUS_ACTIVE, now);
        for (News news : toBeExpired) {
        	System.out.println("上架中改為過期，ID: " + news.getNewsId() + "，標題: " + news.getNewsTitle());
        	news.setNewsStatus(new NewsStatus(STATUS_EXPIRED));
        }

        // 如果有異動，統一批次更新到資料庫
        if (!toBeActived.isEmpty() || !toBeExpired.isEmpty()) {
        	List<News> allUpdates = new ArrayList<>();
        	allUpdates.addAll(toBeActived);
        	allUpdates.addAll(toBeExpired);
        	
        	//批次更新
        	repository.saveAll(allUpdates); 
        	  System.out.println("自動更新消息狀態完成，共更新 " + allUpdates.size() + " 筆");
        }else {
        System.out.println("無需更新消息狀態");
        }
	}
	
//	// 每小時執行一次，自動更新消息狀態
	@Scheduled(fixedRate = 60 * 60 * 1000) // 單位為毫秒
	@Transactional
	public void autoUpdateNewsStatusScheduler() {
	    updateNewsStatusAutomatically();
	}
//
//	// Spring Boot 啟動時立即執行一次
	@PostConstruct
	@Transactional
	public void autoUpdateNewsStatusOnStartup() {
	    updateNewsStatusAutomatically();
	}

	
	// 將DTO 轉換成 VO 存入資料庫
	private News dtoToNewsVO(NewsDTO dto) {
		News news = new News();
		news.setAdminId(dto.getAdminId());
		news.setNewsTitle(dto.getNewsTitle());
		news.setNewsId(dto.getNewsId());
		news.setNewsContent(dto.getNewsContent());
		news.setNewsStartDate(dto.getNewsStartDate());
		news.setNewsEndDate(dto.getNewsEndDate());
		news.setCreatedTime(dto.getCreatedTime());
		news.setNewsImg(dto.getNewsImg());

		// 處理分類關聯
		NewsCategory category = new NewsCategory();
		category.setNewsCategoryId(dto.getNewsCategoryId());
		news.setNewsCategory(category);

		// 處理狀態關聯
		NewsStatus status = new NewsStatus();
		status.setNewsStatusId(dto.getNewsStatusId());
		news.setNewsStatus(status);

		return news;
	}

	// 資料回傳前端VO轉換成DTO
	private NewsDTO voToNewsDTO(News vo) {
		NewsDTO newsDto = new NewsDTO();
		newsDto.setAdminId(vo.getAdminId());
		newsDto.setNewsId(vo.getNewsId());
		newsDto.setNewsTitle(vo.getNewsTitle());
		newsDto.setNewsContent(vo.getNewsContent());
		newsDto.setNewsStartDate(vo.getNewsStartDate());
		newsDto.setNewsEndDate(vo.getNewsEndDate());
		newsDto.setCreatedTime(vo.getCreatedTime());
		newsDto.setNewsImg(vo.getNewsImg());
		// ID
		newsDto.setNewsCategoryId(vo.getNewsCategory().getNewsCategoryId());
		newsDto.setNewsStatusId(vo.getNewsStatus().getNewsStatusId());

		// 名稱
		newsDto.setNewsCategoryName(vo.getNewsCategory().getCategoryName());
		newsDto.setNewsStatusName(vo.getNewsStatus().getStatusName());
		return newsDto;
	}

	// SetVO轉ListDTO
	public List<NewsDTO> voToDto(Collection<News> voList) {
		List<NewsDTO> newsListDTO = new ArrayList<NewsDTO>();
		for (News vo : voList) {
			newsListDTO.add(voToNewsDTO(vo));
		}
		return newsListDTO;
	}

}
