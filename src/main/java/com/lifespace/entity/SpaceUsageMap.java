package com.lifespace.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "space_usage_map")
public class SpaceUsageMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_mapping_id")
    private Integer usageMappingId;

    @ManyToOne
    @JoinColumn(name = "space_id", referencedColumnName = "space_id")
    @JsonBackReference
    private Space space;

    @ManyToOne
    @JoinColumn(name = "space_usage_id", referencedColumnName = "space_usage_id")
    @JsonIgnoreProperties("spaceUsageMaps")  // 避免遞迴，多對多綁2個表格以上請用JsonIgnoreProperties，不然用JsonBack，Space會抓不到SpaceUsage
    private SpaceUsage spaceUsage;

    @Column(name = "created_time", insertable = false)
    @CreationTimestamp
    private Timestamp createdTime;

    public Integer getUsageMappingId() {
        return usageMappingId;
    }

    public void setUsageMappingId(Integer usageMappingId) {
        this.usageMappingId = usageMappingId;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public SpaceUsage getSpaceUsage() {
        return spaceUsage;
    }

    public void setSpaceUsage(SpaceUsage spaceUsage) {
        this.spaceUsage = spaceUsage;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
}
