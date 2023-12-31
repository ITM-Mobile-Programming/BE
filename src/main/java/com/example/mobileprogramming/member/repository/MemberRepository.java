package com.example.mobileprogramming.member.repository;

import com.example.mobileprogramming.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByCode(String code);

    void deleteByEmail(String email);
}
