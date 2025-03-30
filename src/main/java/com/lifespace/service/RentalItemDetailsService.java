package com.lifespace.service;

import com.lifespace.repository.RentalItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentalItemDetailsService {

    @Autowired
    private RentalItemRepository RentalItemRepository;
}
