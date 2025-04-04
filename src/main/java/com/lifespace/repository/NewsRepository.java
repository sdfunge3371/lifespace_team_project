// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.lifespace.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifespace.entity.NewsVO;

@Repository
public interface NewsRepository extends JpaRepository<NewsVO, String> {

	// 自定義方法依類別與狀態取得NewsVO
	@Query("FROM NewsVO n WHERE n.newsCategory.newsCategoryId = :categoryId AND n.newsStatus.newsStatusId = :statusId")
	List<NewsVO> findReqColumn(@Param("categoryId") String newsCategoryId,
	                           @Param("statusId") Integer newsStatusId);


	// 只篩類別
	@Query("FROM NewsVO n WHERE n.newsCategory.newsCategoryId = :categoryId")
	List<NewsVO> findByCategory(@Param("categoryId") String categoryId);

	// 只篩狀態
	@Query("FROM NewsVO n WHERE n.newsStatus.newsStatusId = :statusId")
	List<NewsVO> findByStatus(@Param("statusId") Integer statusId);
	
	// 查詢上架/下架條件符合的消息(Timestamp now:比對「現在時間」是否超過起始日)
	// 狀態為"即將上架(2)"，但起始時間已到 → 該上架了!
	// NewsStatus_NewsStatusId → NewsVO.newsStatus.newsStatusId（透過關聯屬性newsStatus取內部欄位）
	// 等價JPQL查詢
	//	SELECT n FROM NewsVO n
	//	WHERE n.newsStatus.newsStatusId = :statusId
	//	AND n.newsStartDate < :now
	List<NewsVO> findByNewsStatus_NewsStatusIdAndNewsStartDateBefore(Integer newsStatusId, Timestamp now);
	
	//	狀態為"上架中(1)"，但結束時間已過 → 該下架了!
	List<NewsVO> findByNewsStatus_NewsStatusIdAndNewsEndDateBefore(Integer newsStatusId, Timestamp now);

}
