package com.eshop.enums;

/**
 * Created by windvalley on 2018/8/5.
 */
public enum PayFlatformEnum {
    ALIPAY(1, "支付宝"),
    WEICHAT(2, "微信"),
    ;

    private final int code;

    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    PayFlatformEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
