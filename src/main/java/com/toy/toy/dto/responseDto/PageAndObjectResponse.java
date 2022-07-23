package com.toy.toy.dto.responseDto;

import com.toy.toy.StaticVariable;
import com.toy.toy.service.PageCalculator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.toy.toy.StaticVariable.*;


@Getter
@Slf4j
public class PageAndObjectResponse<T> {
    private T content;
    private List<EntityModel> pageInfo;

    public  PageAndObjectResponse(T content , PageCalculator pageCalculator) {
        this.content = content;
        pageInfo = new ArrayList<>();
        getEntityModelPageResponse(pageCalculator);
    }



    public void getEntityModelPageResponse(PageCalculator pageCalculator){


        int startPageNum = pageCalculator.getStartPageNum();
        int endPageNum = pageCalculator.getEndPageNum();
        int pageSize = pageCalculator.getPageSize();




        for(int i=startPageNum-1 ; i<endPageNum ; i++) {
            HashMap<String , Integer> pageLink = new HashMap<>();

            pageLink.put("pageNum", i + 1);

            EntityModel<HashMap<String, Integer>> page = EntityModel.of(pageLink)
                    .add(Link.of("http://www.localhost:8080/boards?page=" + i).withSelfRel());

            pageInfo.add(page);
        }


        //이전 페이지 링크
        if(pageCalculator.isPrevious()){
            HashMap<String , String> pageLink = new HashMap<>();

            pageLink.put("pageNum", "이전");

            EntityModel<HashMap<String, String>> page = EntityModel.of(pageLink)
                    .add(Link.of("http://www.localhost:8080?page=" + (startPageNum - pageSize - 1))
                            .withSelfRel());

            pageInfo.add(page);
        }

        //다음 페이지 링크
        if(pageCalculator.isNext()){
            HashMap<String , String> pageLink = new HashMap<>();

            pageLink.put("pageNum", "다음");

            EntityModel<HashMap<String, String>> page = EntityModel.of(pageLink)
                    .add(Link.of("http://www.localhost:8080?page=" + (startPageNum + pageSize - 1))
                            .withSelfRel());


            pageInfo.add(page);

        }


    }

}
