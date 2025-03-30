package com.lifespace.repository;

import com.lifespace.entity.RentalItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalItemRepository extends JpaRepository<RentalItem, String> {
}
