package com.lifespace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

//這個是通用的 Email 發送工具，只負責發送信件，各自的邏輯要各自寫

@Service
public class MailService {
	
    @Autowired
    private JavaMailSender mailSender;

    //忘記密碼的驗證碼發送
    public void sendResetLink(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("重設密碼連結");
        message.setText("請點擊以下連結在 10 分鐘內重設密碼：" + resetLink);
        mailSender.send(message);
    }
    
  //跟活動相關的email發送
    public void eventMemberNotification(
            String status,
            String memberName,
            String eventName,
            String toEmail) {

    	 try {
    	        MimeMessage mimeMessage = mailSender.createMimeMessage();
    	        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

    	        helper.setTo(toEmail);

    	        String subject = "";
    	        String messageBody = "";
    	        String buttonText = "前往活動管理頁面";
    	        String buttonUrl = "https://34.80.209.234/events_for_user.html"; 
    	        String themeColor = "#4CAF50"; // 綠色

    	        switch (status) {
    	            case "成功候補":
    	                subject = "成功候補活動通知";
    	                messageBody = memberName + " 使用者您好，您已成功候補活動： <strong>" + eventName + "</strong>。"
    	                        + "<br>請至活動參與頁面查看是否已出現在「已報名活動」區塊。"
    	                        + "<br>若要取消報名請自行取消，謝謝您的參與。";
    	                break;
    	            case "活動取消":
    	                subject = "活動取消通知";
    	                messageBody = memberName + " 使用者您好，很遺憾活動： <strong>" + eventName + "</strong> 已被主辦者取消。"
    	                        + "<br>您可以至活動總覽頁面瀏覽其他有趣活動，期待下次與您相遇！";
    	                themeColor = "#f44336"; // 紅色
    	                buttonText = "前往活動總覽頁面";
    	                buttonUrl = "https://34.80.209.234/event_overview.html";
    	                break;
    	            case "活動開始通知":
    	                subject = "活動即將開始提醒";
    	                messageBody = memberName + " 使用者您好，您報名的活動：<strong>" + eventName + "</strong> 即將開始！"
    	                        + "<br>請確認活動時間，準時參與，祝您活動愉快！";
    	                themeColor = "#2196F3"; // 藍色
    	                break;
    	            default:
    	                return;
    	        }

    	        helper.setSubject(subject);

    	        String html = "<div style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;'>"
    	                + "<div style='max-width: 600px; margin: auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>"
    	                + "<img src='cid:lifespace-logo' style='height: 60px; display: block; margin-bottom: 20px;'>"
    	                + "<h2 style='color: " + themeColor + ";'>" + subject + "</h2>"
    	                + "<p style='font-size: 16px; line-height: 1.6;'>" + messageBody + "</p>"
    	                + "<br>"
    	                + "<a href='" + buttonUrl + "' style='display: inline-block; background-color: " + themeColor + "; color: white; padding: 12px 20px; text-decoration: none; border-radius: 6px;'>"
    	                + buttonText + "</a>"
    	                + "<br><br><hr style='border:none;border-top:1px solid #ccc'>"
    	                + "<p style='color: gray; font-size: 12px;'>此為系統自動發送通知，請勿直接回覆此信件。</p>"
    	                + "<p style='color: gray; font-size: 12px;'>LifeSpace 團隊敬上</p>"
    	                + "</div></div>";

    	        helper.setText(html, true);

    	        // 內嵌圖片 logo（放在 resources/static）
    	        ClassPathResource logo = new ClassPathResource("static/images/img.bootstrap/LifeSpace3.png");
    	        helper.addInline("lifespace-logo", logo);

    	        mailSender.send(mimeMessage);

    	    } catch (MessagingException e) {
    	        e.printStackTrace();
    	    }
    }

}
