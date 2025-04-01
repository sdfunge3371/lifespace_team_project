package com.lifespace.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "member")
public class Member implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "member_id")
	private String memberId;
	
	@Column(name = "member_name")
	private String memberName;
	
	@Column(name = "member_image", columnDefinition = "blob")
	private byte[] memberImage;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "registration_time")
	@CreationTimestamp
	private Timestamp registrationTime;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "account_status")
	private Integer accountStatus;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "birthday")
	private LocalDate birthday;
	
	//FK(有需要再解開註解用)----------------------
	
	//留言按讚紀錄表(會員跟留言之間的中介表)
//	@OneToMany(mappedBy = "member" ,cascade = CascadeType.ALL)
//	private List<CommentLike > commentLike;
	
	//留言檢舉紀錄表(會員跟留言之間的中介表)
//	@OneToMany(mappedBy = "member" ,cascade = CascadeType.ALL)
//	private List<CommentReport > commentReport;
	
	//活動參與會員
//	@OneToMany(mappedBy = "member" ,cascade = CascadeType.ALL)
//	private List<EventMemberEntity> eventMemberEntity;
	
	//活動
//	@OneToMany(mappedBy = "member" ,cascade = CascadeType.ALL)
//	private List<Event> event;
	
	//訂單
	@JsonIgnore
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Orders> ordersList;
	
	//空間最愛清單
//  @OneToOne
//  @JoinColumn(name = "favorite_space")  // FK
//  private FavoriteSpace favoriteSpace;
	
	
	
	//getter、setter---------------------------
	
	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public byte[] getMemberImage() {
		return memberImage;
	}

	public void setMemberImage(byte[] memberImage) {
		this.memberImage = memberImage;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(Timestamp registrationTime) {
		this.registrationTime = registrationTime;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday2) {
		this.birthday = birthday2;
	}

	public List<Orders> getOrdersList() {
		return ordersList;
	}

	public void setOrdersList(List<Orders> ordersList) {
		this.ordersList = ordersList;
	}
}
