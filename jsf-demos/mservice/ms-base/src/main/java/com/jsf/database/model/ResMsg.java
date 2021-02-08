package com.jsf.database.model;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-03
 * Time: 13:26
 */
public class ResMsg  {

    private int code;

    private String msg;

    private Object data;

    public static ResMsg success() {
        return new ResMsg(0, "SUCCESS");
    }

    public static ResMsg fail() {
        return new ResMsg(-1, "FAIL");
    }

    public static ResMsg fail(int code, String msg) {
        return new ResMsg(code, msg);
    }

    public ResMsg() {
    }

    public ResMsg(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResMsg [code=" + code + ", msg=" + msg + "]";
    }

}
