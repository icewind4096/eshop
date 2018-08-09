package com.eshop.enums;

/**
 * Created by windvalley on 2018/8/6.
 */
public enum PaymentTypeEnum {
    ONLINEPAY(1, "在线支付"),
    ;

    private final int code;

    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static PaymentTypeEnum codeOf(int code){
        for (PaymentTypeEnum paymentTypeEnum: values()){
            if (paymentTypeEnum.getCode() == code){
                return paymentTypeEnum;
            }
        }
        throw new RuntimeException("没有发现对应的支付方式描述");
    }

    PaymentTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
