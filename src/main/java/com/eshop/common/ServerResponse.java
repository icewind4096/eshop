package com.eshop.common;

import com.eshop.enums.ResponseEnum;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by windvalley on 2018/7/14.
 *
*/
//序列化JSON对象时，如果有NULL, 不输出到JSON里面
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    /**
     * 状态
     **/
    private Integer status;

    /**
     * 信息
     **/
    private String message;

    /**
     * 数据
     **/
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private ServerResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    //不在JSON序列化中出现，切记
    @JsonIgnore
    public boolean isSuccess() {
        return status == ResponseEnum.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage(){
        return message;
    }

    public T getData() {
        return data;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createBySuccess(String message, T data){
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String message){
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getCode(), message);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseEnum.ERROR.getCode(), ResponseEnum.ERROR.getMessage());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String message){
        return new ServerResponse<T>(ResponseEnum.ERROR.getCode(), message);
    }

    public static <T> ServerResponse<T> createByErrorMessage(int code, String message){
        return new ServerResponse<T>(code, message);
    }
}
