package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifespace.entity.NewsCategoryVO;

@Repository
public interface NewsCategoryRepository extends JpaRepository<NewsCategoryVO, String> {

}