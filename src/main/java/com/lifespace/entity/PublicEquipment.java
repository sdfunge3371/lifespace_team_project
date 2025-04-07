package com.lifespace.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "public_equipment")
public class PublicEquipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "public_equip_id")
    private Integer publicEquipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "public_equip_name", nullable = false, length = 50)
    private String publicEquipName;

    @Column(name = "created_time")
    private Timestamp createdTime;

    public PublicEquipment() {
    }

    public Integer getPublicEquipId() {
        return publicEquipId;
    }

    public void setPublicEquipId(Integer publicEquipId) {
        this.publicEquipId = publicEquipId;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getPublicEquipName() {
        return publicEquipName;
    }

    public void setPublicEquipName(String publicEquipName) {
        this.publicEquipName = publicEquipName;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
}