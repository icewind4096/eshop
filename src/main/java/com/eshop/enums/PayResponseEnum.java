package com.eshop.enums;

/**
 * Created by windvalley on 2018/8/5.
 */
public enum PayResponseEnum {
    SUCCESS(0, "success"),
    FAIL(10, "fail"),
    ;

    private final int code;

    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    PayResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
