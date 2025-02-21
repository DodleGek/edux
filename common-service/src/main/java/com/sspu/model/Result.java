package com.sspu.model;

import com.sspu.enums.ResultCode;
import lombok.Data;
import lombok.Getter;

@Data // Lombok 插件注解。
public class Result<T> {

    // 状态码。
    private Integer code;

    // 信息。
    private String message;

    // 数据。
    private T data;

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result() {
    }

    public static <T> Result<T> build(Integer code, String message, T resultData) {

        Result<T> result = new Result<>();

        if (resultData != null){
            result.setData(resultData);
        }

        result.setCode(code);
        result.setMessage(message);

        return result;
    }

    public static Result success(){
        Result result = new Result();
        result.setCode(ResultCode.SUCCESS.getResultCode());
        result.setMessage(ResultCode.SUCCESS.getResultMsg());
        return result;
    }

    public static<T> Result<T> success(T resultData){
        Result<T> result = build(ResultCode.SUCCESS.getResultCode(), ResultCode.SUCCESS.getResultMsg(), resultData);
        return result;
    }

    public static Result fail(){
        Result result = new Result();
        result.setCode(ResultCode.FAIL.getResultCode());
        result.setMessage(ResultCode.FAIL.getResultMsg());
        return result;
    }

    //失败的方法
    public static<T> Result<T> fail(T resultData) {
        return build(ResultCode.FAIL.getResultCode(), ResultCode.FAIL.getResultMsg(), resultData);
    }
}

