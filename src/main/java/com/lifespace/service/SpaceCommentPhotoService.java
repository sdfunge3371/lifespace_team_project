package com.lifespace.service;

import com.lifespace.repository.SpaceCommentPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpaceCommentPhotoService {

    @Autowired
    private SpaceCommentPhotoRepository spaceCommentPhotoRepository;
}
