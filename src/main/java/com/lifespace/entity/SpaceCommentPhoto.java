package com.lifespace.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "space_comment_photo")
public class SpaceCommentPhoto {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name= "space_comment_photo_id")
    private String spaceCommentPhotoId;

    @Column(name = "space_photo")
    private byte[] spacePhoto;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orders;

    public String getSpaceCommentPhotoId() {
        return spaceCommentPhotoId;
    }

    public void setSpaceCommentPhotoId(String spaceCommentPhotoId) {
        this.spaceCommentPhotoId = spaceCommentPhotoId;
    }


    public byte[] getSpacePhoto() {
        return spacePhoto;
    }

    public void setSpacePhoto(byte[] spacePhoto) {
        this.spacePhoto = spacePhoto;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
}
