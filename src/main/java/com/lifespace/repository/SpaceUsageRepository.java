package com.lifespace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifespace.constant.SpaceUsageStatus;
import com.lifespace.entity.SpaceUsage;

public interface SpaceUsageRepository extends JpaRepository<SpaceUsage, String>{
	// JpaRepository 內建：
    // - save(User user) 這邊只要新增 
    // - findById(Integer id)
    // - findAll()
    // - deleteById(Integer id)
	
	List<SpaceUsage> findBySpaceUsageStatus(SpaceUsageStatus spaceUsageStatus);// 只找出「可用」的項目，並取代findAll()
	
    Optional<SpaceUsage> findBySpaceUsageName(String name);

//    List<SpaceUsage> findAllBySpaceUsageId(List<String> spaceUsageIds);
}
