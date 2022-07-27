package com.toy.toy.repository;


import com.toy.toy.entity.Board;
import com.toy.toy.entity.Comment;
import com.toy.toy.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //회원이 삭제 될때 회원이 작성한 게시글들 삭제
    @Modifying
    @Query("delete from Comment c where c.member=:member")
    public void deleteByMember(@Param("member") Member member);

    //게시글 하나 제거할 떄
    @Modifying
    @Query("delete from Comment c where c.board.id =:boardId")
    public void deleteByBoard(@Param("boardId") Long boardId);


    //회원이 삭제 되면서 게시글이 여러개 삭제될 떄
    @Modifying
    @Query("delete from Comment c where c.board.id IN (:boardId)")
    public void deleteByBoardByMember(@Param("boardId") List<Long> boardId);

    //게시글 Slice도입하자.
    @Query("select c from Comment c where c.board.id = :id")
    public Page<Comment> findByBoardId(@Param("id") Long id , Pageable pageable);
}
