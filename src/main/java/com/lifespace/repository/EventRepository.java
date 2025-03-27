package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifespace.entity.EventEntity;

public interface EventRepository extends JpaRepository<EventEntity,String>{

}
