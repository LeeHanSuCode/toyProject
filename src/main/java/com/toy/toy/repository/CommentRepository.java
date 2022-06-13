package com.toy.toy.repository;


import com.toy.toy.entity.Board;
import com.toy.toy.entity.Comment;
import com.toy.toy.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying
    @Query("delete from Comment c where c.member=:member")
    public void deleteByMember(@Param("member") Member member);


    @Modifying
    @Query("delete from Comment c where c.board=:board")
    public void deleteByBoard(@Param("board") Board board);

    //이렇게 해도 되나??
    @Query("select c from Comment c where c.board.id = :id")
    public List<Comment> findByBoardId(@Param("id") Long id);
}
