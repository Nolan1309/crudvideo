package com.example.crudvideo.service;

import com.example.crudvideo.dao.VideoRepository;
import com.example.crudvideo.entity.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private FirebaseStorageService firebaseFileService;


    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Video getVideoById(int id) {
        return videoRepository.findById(id).orElse(null);
    }

    public Video createVideo(Video video) {
        return videoRepository.save(video);
    }

    public Video updateVideo(int id, Video videoDetails) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video != null) {
            video.setTitle(videoDetails.getTitle());
            video.setDescription(videoDetails.getDescription());
            video.setPath(videoDetails.getPath());
            return videoRepository.save(video);
        }
        return null;
    }

//    public void deleteVideo(int id) {
//        videoRepository.deleteById(id);
//    }

    public List<Video> searchVideosByTitle(String title) {
        return videoRepository.findByTitleContainingIgnoreCase(title);
    }

    public Video uploadVideo(String title, String description, MultipartFile file) throws IOException {
        Video video = firebaseFileService.saveTest(file, title, description);
        return videoRepository.save(video);
    }
    public void xoa(String filePath){
        firebaseFileService.deleteVideoServer(filePath);
    }
    public void deleteVideo(int id) {
        Video video = videoRepository.findById(id).orElse(null);
        String path = firebaseFileService.extractFilePath(video.getPath());
        System.out.println(path);
        if (video != null) {
            try {
                // Xóa video khỏi Firebase Storage
                firebaseFileService.deleteVideoServer(path);
                // Xóa video khỏi cơ sở dữ liệu
                videoRepository.deleteById(id);
            } catch (Exception e) {
                System.err.println("Error deleting video with ID " + id + ": " + e.getMessage());
                throw new RuntimeException("Failed to delete video", e);
            }
        } else {
            System.out.println("Video with ID " + id + " not found.");
        }
    }



}
