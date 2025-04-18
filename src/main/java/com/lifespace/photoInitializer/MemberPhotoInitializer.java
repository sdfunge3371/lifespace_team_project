package com.lifespace.photoInitializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lifespace.entity.Member;
import com.lifespace.repository.MemberRepository;

@Component
public class MemberPhotoInitializer implements CommandLineRunner {
	
	 @Autowired
	 private MemberRepository memberRepository;
	 
	 @Transactional
	 public void run(String... args) throws Exception{
		// 指定大頭貼資料夾路徑
	        String folderPathStr = "src/main/resources/static/images/member";
	        Path folderPath = Paths.get(folderPathStr);
	        
	        if (!Files.exists(folderPath)) {
	            System.err.println("找不到資料夾: " + folderPathStr);
	            return;
	        }
	        
	        // 只接受像 M001.jpg、M002.png 這樣的檔名
	        Pattern pattern = Pattern.compile("^(M\\d{3})\\.(jpg|jpeg|png|gif)$", Pattern.CASE_INSENSITIVE);
	        
	        Files.list(folderPath)
            .filter(Files::isRegularFile)
            .forEach(path -> {
                try {
                    String filename = path.getFileName().toString();

                    // 檢查檔名格式
                    if (!pattern.matcher(filename).matches()) {
                        System.err.println("檔名格式錯誤，跳過: " + filename);
                        return;
                    }

                    // 從檔名取得 memberId
                    String memberId = filename.substring(0, filename.lastIndexOf('.'));

                    // 找出該會員
                    Optional<Member> optionalMember = memberRepository.findById(memberId);
                    if (optionalMember.isEmpty()) {
                        System.err.println("找不到會員: " + memberId);
                        return;
                    }

                    Member member = optionalMember.get();

                    // 如果已經有圖片就跳過
                    if (member.getMemberImage() != null) {
                        System.out.println("已經有圖片，不重複寫入: " + memberId);
                        return;
                    }

                    // 讀取圖片檔案
                    byte[] imageBytes = Files.readAllBytes(path);

                    // 存進 memberImg
                    member.setMemberImage(imageBytes);
                    memberRepository.save(member);

                    System.out.println("成功儲存大頭貼: " + memberId);

                } catch (Exception e) {
                    System.err.println("處理失敗: " + path.getFileName() + " → " + e.getMessage());
                }
            });
	        
	        
	        
	        
		 
	 }


}
