package com.jsf.utils.entity;

/**
 * 接口返回结果封装类
 *
 * @author rick
 */
public class ResMsg {

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private Object data;

    static ResMsg SUCCESS = new ResMsg(DefaultResCode.SUCCESS.code(), DefaultResCode.SUCCESS.msg());
    static ResMsg FAIL = new ResMsg(DefaultResCode.FAIL.code(), DefaultResCode.FAIL.msg());

    /**
     * 成功
     * code=0
     *
     * @return
     */
    public static ResMsg success() {
        return SUCCESS;
    }

    /**
     * 成功
     * 自定义消息
     *
     * @param msg 成功消息
     * @return
     */
    public static ResMsg success(String msg) {
        return new ResMsg(DefaultResCode.SUCCESS.code(), msg);
    }

    /**
     * 成功
     * 返回数据
     *
     * @param data 返回数据
     * @return
     */
    public static ResMsg successdata(Object data) {
        return new ResMsg(DefaultResCode.SUCCESS.code(), DefaultResCode.SUCCESS.msg(), data);
    }

    /**
     * 成功
     * 自定义消息&返回数据
     *
     * @param msg  成功消息
     * @param data 返回数据
     * @return
     */
    public static ResMsg successdata(String msg, Object data) {
        return new ResMsg(DefaultResCode.SUCCESS.code(), msg, data);
    }

    /**
     * 失败
     * code > 1 | code = -1
     *
     * @return
     */
    public static ResMsg fail() {
        return FAIL;
    }

    /**
     * 失败
     * 通常是操作失败，如更新失败
     *
     * @param msg
     * @return
     */
    public static ResMsg fail(String msg) {
        return new ResMsg(DefaultResCode.FAIL.code(), msg);
    }

    /**
     * 失败
     * 通常是业务系统异常，如参数错误、系统错误、其他异常
     *
     * @param msg
     * @return
     */
    public static ResMsg fail(Integer code, String msg) {
        return new ResMsg(code, msg);
    }


    // 以下是构造方法，建议使用以上封装的静态方法

    public ResMsg(String msg) {
        super();
        this.code = 1;
        this.msg = msg;
    }

    public ResMsg(Integer code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public ResMsg(Integer code, String msg, Object data) {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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
        // return "ResMsg [code=" + code + ", msg=" + msg + ", data=" + data + "]";
        return "ResMsg [code=" + code + ", msg=" + msg + "]";
    }

}
