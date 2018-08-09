package com.eshop.enums;

/**
 * Created by windvalley on 2018/8/5.
 */
public enum OrderStatusEnum {
    CANCEL(0, "订单取消"),
    NOPAY(10, "未支付"),
    PAY(20, "已支付"),
    SHIPPED(30, "已发货"),
    SUCCESS(40, "已完成"),
    CLOSE(50, "已关闭"),
    ;

    private final int code;

    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    OrderStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static OrderStatusEnum codeOf(Integer status) {
        for (OrderStatusEnum orderStatusEnum: values()){
            if (orderStatusEnum.getCode() == status){
                return orderStatusEnum;
            }
        }
        throw new RuntimeException("没有发现对应的订单状态描述");
    }
}
