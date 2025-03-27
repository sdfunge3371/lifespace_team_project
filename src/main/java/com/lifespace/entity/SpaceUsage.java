package com.lifespace.entity;

import java.sql.Timestamp;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.lifespace.constant.SpaceUsageStatus;

import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "space_usage")
public class SpaceUsage implements java.io.Serializable {
    @Id
    @Column(name = "space_usage_id")
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.lifespace.util.SpaceUsageCustomStringIdGenerator")
    private String spaceUsageId;

    @NotBlank(message = "空間用途不得空白")
    @Column(name = "space_usage_name")  // 配合軟刪除，不需要unique
    private String spaceUsageName;

    @Enumerated(EnumType.STRING)
    @Column(name = "space_usage_status")
    private SpaceUsageStatus spaceUsageStatus = SpaceUsageStatus.AVAILABLE;   // 自ENUM取得預設值「可用」

    @Column(name = "created_time", insertable = false)
    @CreationTimestamp
    private Timestamp createdTime;

    // One to Many
    // 空間用途對照表
    @OneToMany(mappedBy = "spaceUsage", cascade = CascadeType.ALL)
    @OrderBy("usageMappingId asc")
    @JsonManagedReference
    private Set<SpaceUsageMap> spaceUsageMaps;

    // Getters & Setters
    public String getSpaceUsageId() {
        return spaceUsageId;
    }

    public void setSpaceUsageId(String spaceUsageId) {
        this.spaceUsageId = spaceUsageId;
    }

    public String getSpaceUsageName() {
        return spaceUsageName;
    }

    public void setSpaceUsageName(String spaceUsageName) {
        this.spaceUsageName = spaceUsageName;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public SpaceUsageStatus getSpaceUsageStatus() {  // 回傳AVAILABLE, DELETED
        return spaceUsageStatus;
    }

    public void setSpaceUsageStatus(SpaceUsageStatus spaceUsageStatus) {
        this.spaceUsageStatus = spaceUsageStatus;
    }

    // One to Many相關Getters & Setters
    public Set<SpaceUsageMap> getSpaceUsageMaps() {
        return spaceUsageMaps;
    }

    public void setSpaceUsageMaps(Set<SpaceUsageMap> spaceUsageMaps) {
        this.spaceUsageMaps = spaceUsageMaps;
    }
}
