package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifespace.entity.Event;

public interface EventRepository extends JpaRepository<Event,String>{

}
