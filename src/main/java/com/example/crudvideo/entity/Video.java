package com.example.crudvideo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private int id;

    @Column(name = "title_video", length = 255)
    private String title;

    @Column(name = "path_video", columnDefinition = "text")
    private String path;

    @Column(name = "description_video", columnDefinition = "text")
    private String description;

}
