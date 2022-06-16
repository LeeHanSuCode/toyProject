package com.toy.toy.repository;

import com.toy.toy.dto.FilesDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<Files, Long> {

    @Modifying
    @Query("delete from Files f where f.board=:board")
    public void deleteByBoard(@Param("board") Board board);


    @Query("select new com.toy.toy.dto.FilesDto(f.id , f.uploadFilename , f.serverFilename) from Files f where f.board =:board")
    public List<FilesDto> findByBoard(@Param("board") Board board);

    @Query("delete from Files f where f.id IN (:id)")
    public void deleteByIdList(@Param("id") List<Long> id);
}
