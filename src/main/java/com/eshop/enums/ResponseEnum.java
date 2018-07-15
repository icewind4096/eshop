package com.eshop.enums;

/**
 * Created by windvalley on 2018/7/14.
 */
public enum ResponseEnum {
    SUCCESS(0, "SUCCESS"),
    PARAMATERERR(2, "PARAMATER_ERR"),
    ERROR(1, "ERROR"),
    NEEDLOGIN(10, "NEED_LOGIN")
    ;

    private final int code;

    private final String message;

    ResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
