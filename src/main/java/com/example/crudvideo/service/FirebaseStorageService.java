package com.example.crudvideo.service;

import com.example.crudvideo.entity.Video;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private static final String BUCKET_NAME = "thanhsonlab11.appspot.com";

    private Storage storage;

    @EventListener
    public void init(ApplicationReadyEvent event) {
        try {
            ClassPathResource serviceAccount = new ClassPathResource("thanhson.json");
            storage = StorageOptions.newBuilder().
                    setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream())).
                    setProjectId("thanhsonlab11").build().getService();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Video saveTest(MultipartFile file, String title, String description) throws IOException {
        String imageName = generateFileName(file.getOriginalFilename());
        String folderName = "video/";
        String fullFileName = folderName + imageName;

        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", imageName);
        BlobId blobId = BlobId.of(BUCKET_NAME, fullFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setMetadata(map)
                .setContentType(file.getContentType())
                .build();
        storage.create(blobInfo, file.getInputStream());
        String fileUrl = getDownloadUrl(blobInfo);
        Video video = new Video();
        video.setTitle(title);
        video.setPath(fileUrl);
        video.setDescription(description);
        return video;
    }

    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "." + getExtension(originalFileName);
    }

    private String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }

    private String getDownloadUrl(BlobInfo blobInfo) {
        try {
            return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s",
                    blobInfo.getBucket(),
                    URLEncoder.encode(blobInfo.getName(), StandardCharsets.UTF_8.toString()),
                    blobInfo.getMetadata().get("firebaseStorageDownloadTokens"));
        } catch (Exception e) {
            throw new RuntimeException("Error generating download URL", e);
        }
    }

    public void deleteVideoServer(String filePath) {
        try {
            boolean deleted = storage.delete(BUCKET_NAME, filePath);
            if (deleted) {
                System.out.println("Video deleted successfully.");
            } else {
                System.out.println("Video not found or couldn't be deleted.");
            }
        } catch (Exception e) {
            System.err.println("Error deleting video: " + e.getMessage());
        }
    }
    public String extractFilePath(String url) {
        try {
            // Giải mã URL
            String decodedUrl = URLDecoder.decode(url, "UTF-8");

            // Tách phần tên tệp từ URL
            // Ví dụ URL: https://firebasestorage.googleapis.com/v0/b/thanhsonlab11.appspot.com/o/video%2Fa8d9a18c-6ad8-4002-b257-5727144a7f39.mp4?alt=media&token=a8d9a18c-6ad8-4002-b257-5727144a7f39.mp4
            String[] parts = decodedUrl.split("/o/");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid URL format");
            }

            // Phần tên tệp
            String filePath = parts[1].split("\\?")[0];

            return filePath;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error decoding URL", e);
        }
    }

}