// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.lifespace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifespace.entity.Faq;

@Repository
public interface FaqRepository extends JpaRepository<Faq, String> {
    
	// 根據 faqStatus 查詢 FAQ（例如：1 表示上架）
    List<Faq> findByFaqStatus(Integer faqStatus);
}