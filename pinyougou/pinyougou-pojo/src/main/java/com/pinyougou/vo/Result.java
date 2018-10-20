package com.pinyougou.vo;

import java.io.Serializable;

//后台进行操作后,返回信息给前端,操作成功或者失败.
public class Result implements Serializable{

    private Boolean success;
    private String message;

    public Result() {
    }

    //操作成功
    public static Result ok(String message){
       return new Result(true,message);
    }

    //操作失败
    public static Result fail(String message){
        return new Result(false,message);
    }

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
