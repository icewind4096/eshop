package com.eshop.enums;

/**
 * Created by windvalley on 2018/7/26.
 */
public enum CartCheckEnum {
    UNCHECK(0, "购物车物品未选中状态"),
    CHECK(1, "购物车物品选中状态"),
    LIMITNUMFAIL(2, "LIMIT_NUM_FAIL"),
    LIMITNUMSUCCESS(3, "LIMIT_NUM_SUCCESS"),
    ;

    private final int code;

    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    CartCheckEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
