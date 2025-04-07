package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifespace.entity.NewsStatus;

@Repository
public interface NewsStatusRepository extends JpaRepository<NewsStatus, Integer> {

   
}
