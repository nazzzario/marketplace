package com.teamchallenge.marketplace.product.persisit.entity.enums;

public enum SortingFieldEnum {
    // TODO: 10/30/23 add popularity field
    DATE("createdDate"),
    POPULARITY("productTitle");

    private final String filedName;

    SortingFieldEnum(String filedName) {
        this.filedName = filedName;
    }

    public String getFiledName() {
        return filedName;
    }
}
