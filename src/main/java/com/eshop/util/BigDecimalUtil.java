package com.eshop.util;

import java.math.BigDecimal;

/**
 * Created by windvalley on 2018/7/27.
 */
public class BigDecimalUtil {
    private BigDecimalUtil(){

    }

    public static BigDecimal add(double v1, double v2){
        BigDecimal b1 = new BigDecimal(String.valueOf(v1));
        BigDecimal b2 = new BigDecimal(String.valueOf(v2));
        return b1.add(b2);
    }

    public static BigDecimal sub(double v1, double v2){
        BigDecimal b1 = new BigDecimal(String.valueOf(v1));
        BigDecimal b2 = new BigDecimal(String.valueOf(v2));
        return b1.subtract(b2);
    }

    public static BigDecimal mul(double v1, double v2){
        BigDecimal b1 = new BigDecimal(String.valueOf(v1));
        BigDecimal b2 = new BigDecimal(String.valueOf(v2));
        return b1.multiply(b2);
    }

    public static BigDecimal div(double v1, double v2){
        BigDecimal b1 = new BigDecimal(String.valueOf(v1));
        BigDecimal b2 = new BigDecimal(String.valueOf(v2));
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);
    }
}
