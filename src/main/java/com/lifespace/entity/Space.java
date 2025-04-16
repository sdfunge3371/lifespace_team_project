package com.lifespace.entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "space")
public class Space implements java.io.Serializable {

	@Id
	@Column(name = "space_id", updatable = false)
	@GeneratedValue(generator = "custom-id")
	@GenericGenerator(name = "custom-id", strategy = "com.lifespace.util.SpaceCustomStringIdGenerator")
	private String spaceId;

	@NotBlank(message = "分點編號：請勿空白")
	@Column(name = "branch_id", insertable = false, updatable = false)
	private String branchId;

	@ManyToOne
	@JoinColumn(name = "branch_id", referencedColumnName = "branch_id")
	@JsonIgnore
	private Branch branch;
	
	@NotBlank(message = "空間名稱：請勿空白")
	@Column(name = "space_name", unique = true)
	private String spaceName;
	
	@NotNull(message = "空間人數：請勿空白")
	@Min(value = 1, message = "空間人數：需介於1~100之間")
	@Max(value = 100, message = "空間人數：需介於1~100之間")
	@Column(name = "space_people")
	private Integer spacePeople;
	
	@NotNull(message = "空間大小：請勿空白")
	@DecimalMin(value = "0.0", inclusive = false, message = "空間大小：請輸入大於0的值")
	@Column(name = "space_size")
	private Double spaceSize;
	
	@Min(value = 1, message = "時租費率：請輸入大於0的值")
	@Column(name = "space_hourly_fee")
	private Integer spaceHourlyFee;
	
	@Min(value = 1, message = "日租費率：請輸入大於0的值")
	@Column(name = "space_daily_fee")
	private Integer spaceDailyFee;
	
	@Column(name = "space_desc")
	private String spaceDesc;

	@Column(name = "space_rating")
	private Double spaceRating = 0.0;
	
	@Column(name = "space_alert")
	private String spaceAlert;
	
	@NotNull(message = "空間狀態：請勿空白")
	@Column(name = "space_status")
	private Integer spaceStatus;
	
	@Column(name = "space_floor")
	private String spaceFloor;
	
	@Column(name = "created_time", insertable = false)
	@UpdateTimestamp
	private Timestamp createdTime;
	
	// One to many 關聯物件
	// 空間設備
	@OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)   // 注意：一定要加上cascadeType.ALL，才有連動效果，更新關聯資料時，要加上orphanRemoval = true，才能真的更新，否則還是一樣會新增
	@OrderBy("spaceEquipId asc")
	@JsonManagedReference   // SpaceEquipment 的 JSON 不會再塞入 space，避免循環
	private Set<SpaceEquipment> spaceEquipments;  // 利用集合代表含有多筆資料（不要跟ChatGPT一樣用List）

	// 空間用途對照表
	@OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("usageMappingId asc")
	@JsonManagedReference
	private Set<SpaceUsageMap> spaceUsageMaps;

	// 空間照片
	@OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("spacePhotoId asc")
	@JsonManagedReference
	private Set<SpacePhoto> spacePhotos;

	// 空間最愛清單
//	@OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
//	@OrderBy("spacePhotoId asc")
//	@JsonManagedReference
//	private Set<FavoriteSpace> favoritespaces;

	// 訂單
//	@OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
//	@OrderBy("orderId asc")
//	@JsonManagedReference
//	private Set<Orders> orders;

	// 活動
//	@OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
//	@OrderBy("eventId asc")
//	@JsonManagedReference
//	private Set<Event> events;


	// Getters & Setters
	
	public String getSpaceId() {	
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}

	public Integer getSpacePeople() {
		return spacePeople;
	}

	public void setSpacePeople(Integer spacePeople) {
		this.spacePeople = spacePeople;
	}

	public Double getSpaceSize() {
		return spaceSize;
	}

	public void setSpaceSize(Double spaceSize) {
		this.spaceSize = spaceSize;
	}

	public Integer getSpaceHourlyFee() {
		return spaceHourlyFee;
	}

	public void setSpaceHourlyFee(Integer spaceHourlyFee) {
		this.spaceHourlyFee = spaceHourlyFee;
	}

	public Integer getSpaceDailyFee() {
		return spaceDailyFee;
	}

	public void setSpaceDailyFee(Integer spaceDailyFee) {
		this.spaceDailyFee = spaceDailyFee;
	}

	public String getSpaceDesc() {
		return spaceDesc;
	}

	public void setSpaceDesc(String spaceDesc) {
		this.spaceDesc = spaceDesc;
	}

	public Double getSpaceRating() {
		return spaceRating;
	}

	public void setSpaceRating(Double spaceRating) {
		this.spaceRating = spaceRating;
	}

	public String getSpaceAlert() {
		return spaceAlert;
	}

	public void setSpaceAlert(String spaceAlert) {
		this.spaceAlert = spaceAlert;
	}

    public Integer getSpaceStatus() {
        return spaceStatus;
    }
    
	public String getSpaceStatusText() {
		if (spaceStatus == 1) {
			return "上架中";
		} else if (spaceStatus == -1) {
			return "使用中";
		};
		return "未上架";
	}

	public void setSpaceStatus(Integer spaceStatus) {
		this.spaceStatus = spaceStatus;
	}
	
	public void setSpaceStatusText(String spaceStatusText) {
		if ("未上架".equals(spaceStatusText)) {
			this.spaceStatus = 0;
		} else if ("上架中".equals(spaceStatusText)) {
			this.spaceStatus = 1;
		} else {
			this.spaceStatus = -1;
		}
	}

	public String getSpaceFloor() {
		return spaceFloor;
	}

	public void setSpaceFloor(String spaceFloor) {
		this.spaceFloor = spaceFloor;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	// One to many相關的Getters & Setters
	public Set<SpaceEquipment> getSpaceEquipments() {
		return spaceEquipments;
	}

	public void setSpaceEquipments(Set<SpaceEquipment> spaceEquipments) {
		this.spaceEquipments = spaceEquipments;
	}

	public Set<SpaceUsageMap> getSpaceUsageMaps() {
		return spaceUsageMaps;
	}

	public void setSpaceUsageMaps(Set<SpaceUsageMap> spaceUsageMaps) {
		this.spaceUsageMaps = spaceUsageMaps;
	}

	public Set<SpacePhoto> getSpacePhotos() {
		return spacePhotos;
	}

	public void setSpacePhotos(Set<SpacePhoto> spacePhotos) {
		this.spacePhotos = spacePhotos;
	}

	// 取得分點地址
	@Transient
	public String getBranchAddr() {
		return branch.getBranchAddr();
	}

	// 取得分點經緯度
	@Transient
	public Double getLatitude() {
		return branch.getLatitude();
	}

	@Transient
	public Double getLongitude() {
		return branch.getLongitude();
	}

	@Transient
	public List<PublicEquipment> getPublicEquipments() {
		return branch.getPublicEquipments();
	}

	@Transient
	public List<RentalItem> getRentalItems() {
		return branch.getRentalItems();
	}

	@Transient
	public String getBranchName() {
		return branch.getBranchName();
	}

	@Transient
	public Integer getBranchStatus() {
		return branch.getBranchStatus();
	}

	// 取得分點公共設備

//	public Set<Orders> getOrders() {
//		return orders;
//	}
//
//	public void setOrders(Set<Orders> orders) {
//		this.orders = orders;
//	}

//	public Set<Event> getEvents() {
//		return events;
//	}
//
//	public void setEvents(Set<Event> events) {
//		this.events = events;
//	}
}
