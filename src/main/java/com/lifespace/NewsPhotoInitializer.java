package com.lifespace;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.lifespace.entity.News;
import com.lifespace.repository.NewsRepository;

import jakarta.transaction.Transactional;

@Component // 標示為 Spring 管理的元件（啟動時自動執行）
public class NewsPhotoInitializer implements CommandLineRunner {

    @Autowired
    private NewsRepository newsRepository;

    @Override
    @Transactional // 資料庫操作需要交易管理
    public void run(String... args) throws Exception {
        // 檢查是否已有消息資料（沒有就不做匯入）
        long count = newsRepository.count();
        if (count == 0) {
            System.out.println("無任何消息資料，不載入圖片");
            return;
        }

        // 設定圖片資料夾位置（本機相對路徑）
        String folderPathStr = "src/main/resources/static/images/newsPhoto/";
        Path folderPath = Paths.get(folderPathStr);

        // 若資料夾不存在，顯示錯誤訊息並結束
        if (!Files.exists(folderPath)) {
            System.err.println(" 找不到資料夾: " + folderPathStr);
            return;
        }

        // 建立正規表示式：只接受像 N001.jpg、N002.png 這樣的檔名
        Pattern pattern = Pattern.compile("^(N\\d{3})\\.(jpg|jpeg|png|gif)$", Pattern.CASE_INSENSITIVE);

        // 走訪該資料夾底下所有檔案（只處理普通檔案）
        Files.list(folderPath)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        // 取得檔案名稱
                        String filename = path.getFileName().toString();

                        // 檢查檔名格式是否符合（如果不符合就跳過）
                        if (!pattern.matcher(filename).matches()) {
                            System.err.println("檔名格式不符，略過: " + filename);
                            return;
                        }

                        // 從檔名中擷取出 newsId，例如從 "N001.jpg" → "N001"
                        String newsId = filename.substring(0, filename.lastIndexOf('.'));

                        // 從資料庫查詢對應的News資料
                        Optional<News> optionalNews = newsRepository.findById(newsId);
                        if (optionalNews.isEmpty()) {
                            System.err.println("找不到 NewsID: " + newsId);
                            return;
                        }

                        News news = optionalNews.get();

                        // 如果該消息已經有圖片，就不再重複寫入
                        if (news.getNewsImg() != null) {
                            System.out.println("已有圖片，不重複寫入: " + newsId);
                            return;
                        }

                        // 讀取圖片檔案轉成 byte[]
                        byte[] imageBytes = Files.readAllBytes(path);

                        // 寫入資料庫
                        news.setNewsImg(imageBytes);
                        newsRepository.save(news);

                        System.out.println("圖片已儲存到消息: " + newsId);

                    } catch (Exception e) {
                        // 處理圖片過程中出錯，顯示錯誤訊息
                        System.err.println("處理檔案失敗: " + path.getFileName() + " → " + e.getMessage());
                    }
                });
    }
}
