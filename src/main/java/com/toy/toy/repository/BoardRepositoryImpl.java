package com.toy.toy.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.toy.toy.dto.SearchConditionDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.toy.toy.entity.QBoard.board;
import static com.toy.toy.entity.QMember.member;

@RequiredArgsConstructor
@Slf4j

public class BoardRepositoryImpl implements BoardRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Board> findByCond(Pageable pageable, SearchConditionDto searchConditionDto) {

        List<OrderSpecifier> allOrderSpecifiers = getAllOrderSpecifiers(pageable);


        List<Board> content = jpaQueryFactory
                .selectFrom(board)
                .join(board.member, member).fetchJoin()
                .where(
                        userIdCond(searchConditionDto.getUserId()),
                        subjectCond(searchConditionDto.getSubject())
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(allOrderSpecifiers.stream().toArray(OrderSpecifier[]::new))
                .fetch();



        //countQuery
        Long count = jpaQueryFactory
                .select(board.count())
                .from(board)
                .where(
                        userIdCond(searchConditionDto.getUserId()),
                        subjectCond(searchConditionDto.getSubject())
                ).fetchOne();

        return new PageImpl<>(content,pageable,count);
    }

    //검색 조건
    private BooleanExpression userIdCond(String userId){
        return StringUtils.hasText(userId) ? board.member.userId.contains(userId) : null;
    }
    //검색 조건
    private BooleanExpression subjectCond(String subject){
        return StringUtils.hasText(subject) ? board.subject.contains(subject) : null;
    }

    //정렬 조건 검증 및 반환(정렬 조건으로는 날짜와 조회 순으로만 처리하고 , 그 밖의 정렬 조건일 경우
    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable){

        List<OrderSpecifier> orderBys= new ArrayList<>();



        if(!pageable.getSort().isEmpty()){
            Sort sort = pageable.getSort();
            List<Sort.Order> sortOrder = sort.get().collect(Collectors.toList());

            Order direction = sortOrder.get(0).getDirection().isAscending() ? Order.ASC : Order.DESC;


            if(sortOrder.get(0).getProperty().equals("createdDate")){
                OrderSpecifier<?> orderByCreatedDate = QueryDslOrderUtilCustom.getSortedColumn(direction , board,"createdDate");
                orderBys.add(orderByCreatedDate);
            }

            if(sortOrder.get(0).getProperty().equals("readCount")){
                OrderSpecifier<?> orderByReadCount = QueryDslOrderUtilCustom.getSortedColumn(direction , board,"readCount");
                orderBys.add(orderByReadCount);
            }


            OrderSpecifier<?> orderById = QueryDslOrderUtilCustom.getSortedColumn(Order.DESC , board,"id");
            orderBys.add(orderById);

        }

        return orderBys;
    }
}
