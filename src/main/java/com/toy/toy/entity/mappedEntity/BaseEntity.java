package com.toy.toy.entity.mappedEntity;

import lombok.Getter;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class BaseEntity {

    //Auditing 활용해서 넣기

    protected LocalDateTime createdDate;
    protected LocalDateTime updatedDate;
}
