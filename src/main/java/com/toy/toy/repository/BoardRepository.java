package com.toy.toy.repository;

import com.toy.toy.entity.Board;
import com.toy.toy.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from Board b where b.member = :member")
    public void deleteByMemberId(@Param("member") Member member);

    @Query("select b from Board b where b.member = :member")
    public List<Board> findByMember(@Param("member") Member member);
}
