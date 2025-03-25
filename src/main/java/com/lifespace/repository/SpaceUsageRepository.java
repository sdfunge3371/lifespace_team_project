package com.lifespace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifespace.model.SpaceUsage;

public interface SpaceUsageRepository extends JpaRepository<SpaceUsage, String>{
	// JpaRepository 內建：
    // - save(User user) 這邊只要新增 
    // - findById(Integer id)
    // - findAll()
    // - deleteById(Integer id)
	
    Optional<SpaceUsage> findBySpaceUsageName(String name);

}
