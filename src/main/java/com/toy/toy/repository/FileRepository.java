package com.toy.toy.repository;

import com.toy.toy.dto.responseDto.FilesResponse;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<Files, Long> {

    @Modifying
    @Query("delete from Files f where f.board.id=:boardId")
    public void deleteByBoard(@Param("boardId") Long boardId);

    @Modifying
    @Query("delete from Files f where f.board.id IN (:boardId)")
    public void deleteByBoardByMember(@Param("boardId") List<Long> boardId);


    @Query("select f from Files f where f.board =:board")
    public List<Files> findByBoard(@Param("board") Board board);

    @Query("delete from Files f where f.id IN (:id)")
    public void deleteByIdList(@Param("id") List<Long> id);
}
