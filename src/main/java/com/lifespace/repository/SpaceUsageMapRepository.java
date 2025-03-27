package com.lifespace.repository;

import com.lifespace.entity.Space;
import com.lifespace.entity.SpaceUsageMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SpaceUsageMapRepository extends JpaRepository<SpaceUsageMap, Integer> {
    // JpaRepository 內建：
    // - save(User user)
    // - findById(Integer id)
    // - findAll()
    // - deleteById(Integer id)

    List<SpaceUsageMap> findBySpace(Space space);
}
