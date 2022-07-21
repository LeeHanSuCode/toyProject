package com.toy.toy.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

import java.util.List;

public class QueryDslOrderUtilCustom {

    //Path 파라미터는 compileQuerydsl 빌드를 통해서 생성된 Q타입 클래스의 객체
    //Sort의 대상이 되는 Q타입 클래스 객체를 전달한다.

    public static OrderSpecifier<?> getSortedColumn(Order order , Path<?> parent , String fieldName){
        Path<Object> fieldPath = Expressions.path(Object.class, parent , fieldName);

        return new OrderSpecifier(order , fieldPath);
    }

}
