package com.lifespace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifespace.entity.Space;

public interface SpaceRepository extends JpaRepository<Space, String> {
	// JpaRepository 內建：
	// - save(User user)
	// - findById(Integer id)
	// - findAll()
	// - deleteById(Integer id)

	Optional<Space> findBySpaceName(String spaceName);
}