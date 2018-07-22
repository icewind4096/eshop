package com.eshop.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by windvalley on 2018/7/22.
 */
public class DateTimeUtil {
    public static final String STANDFORMATE = "yyyy-MM-dd HH:mm:ss";

    public static Date stringToDate(String dateTimeString){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDFORMATE);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeString);
        return dateTime.toDate();
    }

    public static String dateTostring(Date date){
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDFORMATE);
    }

    public static void main(String[] args){
        System.out.println(DateTimeUtil.dateTostring(new Date()));
        System.out.println(DateTimeUtil.stringToDate("2016-10-11 12:13:14"));
    }
}
