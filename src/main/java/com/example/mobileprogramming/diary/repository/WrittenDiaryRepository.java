package com.example.mobileprogramming.diary.repository;

import com.example.mobileprogramming.diary.entity.WrittenDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WrittenDiaryRepository extends JpaRepository<WrittenDiary, Long> {
}
