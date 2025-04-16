package com.lifespace;

import com.lifespace.entity.Space;
import com.lifespace.entity.SpacePhoto;
import com.lifespace.repository.SpacePhotoRepository;
import com.lifespace.repository.SpaceRepository;
import com.lifespace.service.SpacePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SpacePhotoInitializer implements CommandLineRunner {

    @Autowired
    private SpacePhotoRepository spacePhotoRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Override
    public void run(String... args) throws Exception {
        // 只載入一次假資料，不會不斷重複寫入
        if (spacePhotoRepository.count() != 0) {
            System.out.println("已經建過假資料，不再重新初始化照片");
            return;
        }

        String targetPath = "src/main/resources/static/dbimages/space";
        Path folderPath = Paths.get(targetPath);

        if (!Files.exists(folderPath)) {
            System.err.println("找不到資料夾: " + targetPath);
        }

        Pattern pattern = Pattern.compile("^(S\\d{3})_\\d+\\.(jpg|jpeg|png|gif)$", Pattern.CASE_INSENSITIVE);

        Files.list(folderPath)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String filename = path.getFileName().toString();
                        Matcher matcher = pattern.matcher(filename);

                        if (!matcher.matches()) {
                            System.err.println("檔名格式不符，略過: " + filename);
                            return;
                        }

                        String spaceId = matcher.group(1); // e.g., S002

                        Optional<Space> spaceData = spaceRepository.findById(spaceId);
                        if (spaceData.isEmpty()) {
                            System.err.println("找不到空間: " + spaceId);
                            return;
                        }


                        // 檢查該圖片是否已存在
                        Space space = spaceData.get();
                        if (spacePhotoRepository.findBySpaceAndFilename(space, filename).isPresent()) {
                            System.out.println("已存在，不新增: " + filename);
                            return;
                        }

                        // 抓圖
                        byte[] imageBytes = Files.readAllBytes(path);

                        // 存入圖片
                        SpacePhoto photo = new SpacePhoto();
                        photo.setSpace(space);
                        photo.setFilename(filename);
                        photo.setPhoto(imageBytes);

                        spacePhotoRepository.save(photo);

                    } catch (Exception e) {
                        System.err.println("無法處理檔案: " + path.getFileName() + " → " + e.getMessage());
                    }
                });
    }

}
