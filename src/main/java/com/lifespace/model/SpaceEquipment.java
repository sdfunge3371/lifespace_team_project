package com.lifespace.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "space_equipment")
public class SpaceEquipment implements java.io.Serializable {
	
	@Id
	@Column(name = "space_equip_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer spaceEquipId;
	
	// space_id (Many to one)
	@ManyToOne
	@JoinColumn(name = "space_id", referencedColumnName = "space_id")   // space_equipment表格的欄位名稱是"space_id"，對應到space表格的soace_id
	@JsonBackReference   // SpaceEquipment 的 JSON 不會再塞入 space，避免循環
	private Space space;
	// 不用再建立SpaceId實體變數
	
	@NotBlank(message = "空間設備名稱：不得空白")
	@Column(name = "space_equip_name")   // 不能設unique
	private String spaceEquipName;
	
	@Column(name = "space_equip_comment")
	private String spaceEquipComment;
	
	@Column(name = "created_time", insertable = false)
	@CreationTimestamp
	private Timestamp createdTime;
	
	
	public Integer getSpaceEquipId() {
		return spaceEquipId;
	}
	public void setSpaceEquipId(Integer spaceEquipId) {
		this.spaceEquipId = spaceEquipId;
	}
	public Space getSpace() {
		return space;
	}
	public void setSpace(Space space) {
		this.space = space;
	}
	public String getSpaceEquipName() {
		return spaceEquipName;
	}
	public void setSpaceEquipName(String spaceEquipName) {
		this.spaceEquipName = spaceEquipName;
	}
	public String getSpaceEquipComment() {
		return spaceEquipComment;
	}
	public void setSpaceEquipComment(String spaceEquipComment) {
		this.spaceEquipComment = spaceEquipComment;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
}
	