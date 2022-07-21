package com.toy.toy.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

//currentPageNum 은 실제 넘어오는 페이지+1 로 들어온다.

@Getter
@Slf4j
public class PageCalculator {

    private int pageSize;
    private int totalPage;
    private int currentPageNum;

    private int startPageNum;
    private int endPageNum;

    private boolean isNext;
    private boolean isPrevious;

    public PageCalculator(int pageSize , int totalPage , int currentPageNum){
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.currentPageNum =currentPageNum;

        calculate();
        isExist_NextOrPrevious();
    }


    //페이지 번호 얻어오기
    private void calculate(){

        if(currentPageNum % pageSize != 0){
            startPageNum = (currentPageNum/pageSize)*pageSize+1;
        }else{
            startPageNum = (currentPageNum/pageSize-1)*pageSize+1;
        }


        endPageNum = startPageNum + pageSize -1;
        if(endPageNum > totalPage) endPageNum = totalPage;

    }

    //다음과 이전 링크 포함 여부
    private void isExist_NextOrPrevious(){
        if(startPageNum > pageSize) isPrevious = true;
        if(endPageNum < totalPage) isNext = true;
    }



}
