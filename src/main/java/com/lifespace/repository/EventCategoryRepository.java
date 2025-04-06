package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lifespace.entity.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, String>{

}
