package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifespace.entity.NewsCategory;

@Repository
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, String> {

}