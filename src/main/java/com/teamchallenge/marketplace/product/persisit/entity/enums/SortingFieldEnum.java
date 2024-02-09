package com.teamchallenge.marketplace.product.persisit.entity.enums;

import lombok.Getter;

@Getter
public enum SortingFieldEnum {
    DATE("publishDate"),
    POPULARITY("viewCount"),
    RAISE("adRaiseCount"),
    ALL("publishDate");

    private final String filedName;

    SortingFieldEnum(String filedName) {
        this.filedName = filedName;
    }

}
