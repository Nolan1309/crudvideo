package com.example.crudvideo.controller;

import com.example.crudvideo.entity.Video;
import com.example.crudvideo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @GetMapping
    public List<Video> getAllVideos() {
        return videoService.getAllVideos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable int id) {
        Video video = videoService.getVideoById(id);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(video);
    }

    @PostMapping("/upload")
    public Video createVideo(@RequestBody Video video) {
        return videoService.createVideo(video);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable int id, @RequestBody Video videoDetails) {
        Video updatedVideo = videoService.updateVideo(id, videoDetails);
        if (updatedVideo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedVideo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable int id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Video> searchVideosByTitle(@RequestParam String title) {
        return videoService.searchVideosByTitle(title);
    }

    @PostMapping("/uploadVideo")
    public ResponseEntity<Video> uploadVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {

        try {
            Video video = videoService.uploadVideo(title, description, file);
            return ResponseEntity.ok(video);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


}
