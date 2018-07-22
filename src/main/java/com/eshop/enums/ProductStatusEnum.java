package com.eshop.enums;

/**
 * Created by windvalley on 2018/7/22.
 */
public enum ProductStatusEnum {
    ONSALE(1, "在线"),
    ;

    private final int code;

    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ProductStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
