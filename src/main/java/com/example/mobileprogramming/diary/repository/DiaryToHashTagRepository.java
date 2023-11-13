package com.example.mobileprogramming.diary.repository;

import com.example.mobileprogramming.diary.entity.DiaryToHashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryToHashTagRepository extends JpaRepository<DiaryToHashTag, Long> {
}
