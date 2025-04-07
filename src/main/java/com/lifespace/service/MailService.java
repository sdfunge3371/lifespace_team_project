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
    
    //跟活動相關的email發送
    public void eventMemberNotification(
    		String status, 
    		String memberName, 
    		String eventName, 
    		String toEmail) {
    	
    	 SimpleMailMessage message = new SimpleMailMessage();
    	 message.setFrom("mickylumc@gmail.com");
         message.setTo(toEmail);
         
    	switch(status) {
    		case "成功候補":
    			 message.setSubject("成功候補活動通知");
    			 message.setText(memberName + " 使用者您好，您已成功候補活動: " + eventName + 
    					 "\n請到活動參與頁面查看是否已在「已報名活動」頁籤。若要取消報名則須自行取消，感謝您。");
    	         mailSender.send(message);
    			break;
    		case "活動取消":
    			message.setSubject("活動取消通知");
   			 	message.setText(memberName + " 使用者您好，很遺憾活動: " + eventName + " 已被舉辦者取消。" +
   					 "\n可至活動總覽頁面查詢更多您有興趣的活動，感謝您。");
   			 	mailSender.send(message);
    			break;
    		case "活動開始通知":
    			message.setSubject("活動即將舉辦通知");
   			 	message.setText(memberName + " 使用者您好，活動: " + eventName + " 即將開始。" +
   					 "\n請注意活動時間並準時參與。");
   			 	mailSender.send(message);
    			break;
    		default:
    			break;
    	}
    }

}
