package com.example.crudvideo.dao;

import com.example.crudvideo.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video,Integer> {
    List<Video> findByTitleContainingIgnoreCase(String title);
}
