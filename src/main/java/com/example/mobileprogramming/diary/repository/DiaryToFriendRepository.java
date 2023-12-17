package com.example.mobileprogramming.diary.repository;

import com.example.mobileprogramming.diary.entity.Diary;
import com.example.mobileprogramming.diary.entity.DiaryToFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryToFriendRepository extends JpaRepository<DiaryToFriend, Long> {
    Boolean existsByDiaryAndFriendId(Diary diary, Long friendId);

    Optional<DiaryToFriend> findByDiaryAndFriendId(Diary sharedDiary, Long friendId);

    List<DiaryToFriend> findAllByFriendId(Long friendId);
}
