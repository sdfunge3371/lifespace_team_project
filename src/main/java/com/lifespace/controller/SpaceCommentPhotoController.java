package com.lifespace.controller;

import com.lifespace.service.SpaceCommentPhotoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spaceCommentPhoto")
public class SpaceCommentPhotoController {

    SpaceCommentPhotoService spaceCommentPhotoSvc;
}
