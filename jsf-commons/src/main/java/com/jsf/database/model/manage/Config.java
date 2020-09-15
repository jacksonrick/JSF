package com.jsf.database.model.manage;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2018-08-06
 * Time: 11:45
 */
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /** 分组 */
    private String grp;
    /** 键 */
    private String key;
    /** 值 */
    private String val;
    /** 描述 */
    private String descr;
    /** 类型 */
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGrp() {
        return grp;
    }

    public void setGrp(String grp) {
        this.grp = grp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", grp='" + grp + '\'' +
                ", key='" + key + '\'' +
                ", val='" + val + '\'' +
                ", descr='" + descr + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
