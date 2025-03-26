package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.lifespace.model.EventEntity;

public interface EventRepository extends JpaRepository<EventEntity,String>{

}
