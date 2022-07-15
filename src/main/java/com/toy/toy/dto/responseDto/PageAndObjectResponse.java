package com.toy.toy.dto.responseDto;

import com.toy.toy.service.PageCalculator;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
public class PageAndObjectResponse<T> {
    private T content;
    private List<EntityModel> pageInfo;

    public PageAndObjectResponse(T content , PageCalculator pageCalculator) {
        this.content = content;
        pageInfo = new ArrayList<>();
        getEntityModelPageResponse(pageCalculator);
    }



    public void getEntityModelPageResponse(PageCalculator pageCalculator){

        int startPageNum = pageCalculator.getStartPageNum();
        int endPageNum = pageCalculator.getEndPageNum();
        int pageSize = pageCalculator.getPageSize();

        for(int i=startPageNum-1 ; i<endPageNum ; i++){
            Map<String,Integer> page = new HashMap<>();
            page.put("pageNum" , i+1);
            pageInfo.add(
                    EntityModel.of(page)
                            .add(Link.of("http://www.localhost:8080?page=" + i).withRel("pageLink"))
            );
        }


        //이전 페이지 링크
        if(pageCalculator.isPrevious()){
            Map<String,Integer> page = new HashMap<>();
            page.put("previousPageNum" , startPageNum-pageSize);

            pageInfo.add(
                    EntityModel.of(page)
                            .add(Link.of("http://www.localhost:8080?page=" + (startPageNum - pageSize -1))
                                    .withRel("previousPage Link")));
        }

        //다음 페이지 링크
        if(pageCalculator.isNext()){
            Map<String,Integer> page = new HashMap<>();
            page.put("nextPageNum" , startPageNum+pageSize);

            pageInfo.add(
                    EntityModel.of(page)
                            .add(Link.of("http://www.localhost:8080?page=" + (startPageNum + pageSize -1))
                                    .withRel("nextPage Link")));
        }


    }

}
