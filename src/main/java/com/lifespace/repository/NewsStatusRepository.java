package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifespace.entity.NewsStatusVO;

@Repository
public interface NewsStatusRepository extends JpaRepository<NewsStatusVO, Integer> {

   
}
