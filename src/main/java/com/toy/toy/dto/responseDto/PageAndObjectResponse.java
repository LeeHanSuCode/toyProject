package com.toy.toy.dto.responseDto;

import com.toy.toy.StaticVariable;
import com.toy.toy.service.PageCalculator;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.toy.toy.StaticVariable.*;


@Getter
public class PageAndObjectResponse<T> {
    private T content;
    private Map<Object,RepresentationModel> pageInfo;

    public PageAndObjectResponse(T content , PageCalculator pageCalculator) {
        this.content = content;
        pageInfo = new HashMap<>();
        getEntityModelPageResponse(pageCalculator);
    }



    public void getEntityModelPageResponse(PageCalculator pageCalculator){

        int startPageNum = pageCalculator.getStartPageNum();
        int endPageNum = pageCalculator.getEndPageNum();
        int pageSize = pageCalculator.getPageSize();

        for(int i=startPageNum-1 ; i<endPageNum ; i++){
            pageInfo.put(1 ,new RepresentationModel<>()
                    .add(Link.of("http://www.localhost:8080?page=" + i).withRel(PAGE_LINK)));

        /*    pageInfo.add(
                    EntityModel.of(page)
                            .add(Link.of("http://www.localhost:8080?page=" + i).withRel(PAGE_LINK))
            );*/
        }


        //이전 페이지 링크
        if(pageCalculator.isPrevious()){
            pageInfo.put("previousPageNum",
                    new RepresentationModel()
                            .add(Link.of("http://www.localhost:8080?page=" + (startPageNum - pageSize -1))
                                    .withRel(PREVIOUS_PAGE_LINK)));
        }

        //다음 페이지 링크
        if(pageCalculator.isNext()){
            pageInfo.put("previousPageNum",
                    new RepresentationModel()
                            .add(Link.of("http://www.localhost:8080?page=" + (startPageNum + pageSize -1))
                                    .withRel(NEXT_PAGE_LINK)));
        }


    }

}
