package com.lifespace.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.lifespace.entity.Member;
import com.lifespace.repository.MemberRepository;


public class MemberValidator {
	
	private final MemberRepository memberRepositoryError;
	private final String memberIdError;
	private final String memberNameError;
	private final String emailError;
	private final String phoneError;
	private final List<String> errorMessageError = new ArrayList<>();
	
	
	public MemberValidator(MemberRepository memberRepositoryError, String memberIdError, String memberNameError, String emailError, String phoneError) {
		this.memberRepositoryError = memberRepositoryError; 
		this.memberIdError = memberIdError;
		this.memberNameError = memberNameError; 
		this.emailError = emailError; 
		this.phoneError = phoneError;
		validate();
	}
	
	
	public void validateAndThrow() {
	    if (!errorMessageError.isEmpty()) {
	        throw new ValidationException(errorMessageError); // 丟給 Global Handler
	    }
	}

	
	private void validate() {
        // 姓名格式：只允許中文或英文
        if (memberNameError == null || !memberNameError.matches("^[A-Za-z\\u4e00-\\u9fa5]+$")) {
            errorMessageError.add("姓名格式錯誤，只能包含中英文");
        }

        // Email 格式限制 + 不可重複（除了自己）
        if (emailError == null || !emailError.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            errorMessageError.add("Email 格式錯誤，僅限使用 @gmail.com 結尾的信箱");
        } else {
            Optional<Member> existingEmailError = memberRepositoryError.findByEmail(emailError);
            if (existingEmailError.isPresent() && !existingEmailError.get().getMemberId().equals(memberIdError)) {
                errorMessageError.add("Email 已存在");
            }
        }

        
        // Phone 格式限制 + 不可重複（除了自己）
        if (phoneError == null || !phoneError.matches("^\\d{10}$")) {
            errorMessageError.add("電話格式錯誤，需為 10 碼純數字");
        } else {
            Optional<Member> existingPhone = memberRepositoryError.findByPhone(phoneError);
            if (existingPhone.isPresent() && !existingPhone.get().getMemberId().equals(memberIdError)) {
                errorMessageError.add("電話已存在");
            }
        }
        
        
        
        
    }
	
	

}
