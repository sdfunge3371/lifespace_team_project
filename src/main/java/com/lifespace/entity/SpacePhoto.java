package com.lifespace.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "space_photo")
public class SpacePhoto implements Serializable {

    @Id
    @Column(name = "space_photo_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer spacePhotoId;

    @ManyToOne
    @JoinColumn(name = "space_id", referencedColumnName = "space_id")
    @JsonBackReference
    private Space space;

    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB")
    private byte[] photo;

    @Column(name = "created_time", insertable = false)
    @UpdateTimestamp
    private Timestamp createdTime;

    @Column(name = "filename")
    private String filename;   // 用來辨識同一 spaceId 下的同一張圖是否已存

    public Integer getSpacePhotoId() {
        return spacePhotoId;
    }

    public void setSpacePhotoId(Integer spacePhotoId) {
        this.spacePhotoId = spacePhotoId;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
