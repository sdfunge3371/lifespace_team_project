package com.lifespace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifespace.entity.Space;
import com.lifespace.entity.SpaceEquipment;

public interface SpaceEquipmentRepository extends JpaRepository<SpaceEquipment, Object>{
    // JpaRepository 內建：
    // - save(User user)
    // - findById(Integer id)
    // - findAll()
    // - deleteById(Integer id)
	
    List<SpaceEquipment> findBySpace(Space space);   // 搜尋所屬空間的所有空間設備資料

}
