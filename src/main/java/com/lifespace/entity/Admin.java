package com.lifespace.entity;

import java.sql.Timestamp;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")
public class Admin implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "admin_id")
	private String adminId;
	
	@Column(name = "admin_name")
	private String adminName;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "account_status")
	private Integer accountStatus;
	
	@Column(name = "registration_time")
	@CreationTimestamp
	private Timestamp registrationTime;
	
	//FK(有需要再解開註解用)----------------------------------
	
	//常見問題
//	@OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
//	private List<Faq> faq;
//	
    //最新消息
//	@OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
//	private List<NEWS> news;
//	
	//留言檢舉紀錄表(會員跟留言之間的中介表)
//	@OneToMany(mappedBy = "admin" ,cascade = CascadeType.ALL)
//	private List<CommentReport > commentReport;
	
	
	
	//getter、setter---------------------------
	
	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Timestamp getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(Timestamp registrationTime) {
		this.registrationTime = registrationTime;
	}
	

}
