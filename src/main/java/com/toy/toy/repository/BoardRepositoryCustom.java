package com.toy.toy.repository;

import com.toy.toy.dto.SearchConditionDto;
import com.toy.toy.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {

    Page<Board> findByCond(Pageable pageable , SearchConditionDto searchConditionDto);
}
