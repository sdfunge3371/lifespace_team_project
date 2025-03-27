// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.lifespace.entity.FaqVO;


@Repository
public interface FaqRepository extends JpaRepository<FaqVO, String> {
    
//    String findTopByOrderByFaqIdDesc();
}