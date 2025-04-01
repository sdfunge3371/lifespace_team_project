package com.lifespace.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "space_comment_photo")
public class SpaceCommentPhoto {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name= "space_comment_photo_id", updatable = false)
	@GeneratedValue(generator = "space_comment_photo_id")
	@GenericGenerator(name = "space_comment_photo_id", strategy = "com.lifespace.util.SpaceCommentPhotoCustomStringIdGenerator")
    private String spaceCommentPhotoId;

    @Column(name = "space_photo")
    private String spacePhoto;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Orders orders;

	public String getSpaceCommentPhotoId() {
		return spaceCommentPhotoId;
	}

	public void setSpaceCommentPhotoId(String spaceCommentPhotoId) {
		this.spaceCommentPhotoId = spaceCommentPhotoId;
	}

	public String getSpacePhoto() {
		return spacePhoto;
	}

	public void setSpacePhoto(String spacePhoto) {
		this.spacePhoto = spacePhoto;
	}

	public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

	public Orders getOrders() {
		return orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}
}
