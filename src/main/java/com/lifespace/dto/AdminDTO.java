package com.lifespace.dto;

import java.time.LocalDate;

//DTO可以回傳乾淨的內容，以及避免一些重要資訊外流，例如"密碼"
public class AdminDTO {
	
    private String adminId;
    private String adminName;
    private String email;
    private Integer accountStatus;
    private LocalDate registrationTime;
    
    
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
	public LocalDate getRegistrationTime() {
		return registrationTime;
	}
	public void setRegistrationTime(LocalDate registrationTime) {
		this.registrationTime = registrationTime;
	}

	
	

}
