package com.lifespace.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "favorite_space")
public class FavoriteSpace implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_space_id")
    private Integer favoriteSpaceId;

    @Column(name = "space_id")
    private String spaceId;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "created_time", insertable = false, updatable = false)
    private Timestamp createdTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", insertable = false, updatable = false)
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    // 建構子
    public FavoriteSpace() {
    }

    public FavoriteSpace(String spaceId, String memberId) {
        this.spaceId = spaceId;
        this.memberId = memberId;
    }

    // Getters and Setters
    public Integer getFavoriteSpaceId() {
        return favoriteSpaceId;
    }

    public void setFavoriteSpaceId(Integer favoriteSpaceId) {
        this.favoriteSpaceId = favoriteSpaceId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteSpace that = (FavoriteSpace) o;
        return Objects.equals(favoriteSpaceId, that.favoriteSpaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(favoriteSpaceId);
    }

    @Override
    public String toString() {
        return "FavoriteSpace{" +
                "favoriteSpaceId=" + favoriteSpaceId +
                ", spaceId='" + spaceId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}