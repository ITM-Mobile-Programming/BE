package com.example.mobileprogramming.diary.repository;

import com.example.mobileprogramming.diary.entity.Diary;
import com.example.mobileprogramming.diary.entity.WrittenDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface WrittenDiaryRepository extends JpaRepository<WrittenDiary, Long> {
    Optional<WrittenDiary> findTopByMemberIdOrderByCreatedDateDesc(Long memberId);

    Optional<WrittenDiary> findByDiary(Diary diary);

    List<WrittenDiary> findAllByMemberId(Long memberId);

    Long countByMemberId(Long memberId);

    Optional<WrittenDiary> findByMemberIdAndWrittenDate(Long memberId, String writtenDate);

}
