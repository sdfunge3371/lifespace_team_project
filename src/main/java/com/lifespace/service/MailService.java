package com.lifespace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//這個是通用的 Email 發送工具，只負責發送信件，各自的邏輯要各自寫

@Service
public class MailService {
	
    @Autowired
    private JavaMailSender mailSender;

    //忘記密碼的驗證碼發送
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("重設密碼驗證碼");
        message.setText("您的驗證碼是：" + code + "\n請在10分鐘內使用此驗證碼完成重設密碼流程。");

        mailSender.send(message);
    }

}
