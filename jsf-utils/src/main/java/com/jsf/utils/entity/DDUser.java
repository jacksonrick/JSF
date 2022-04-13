package com.jsf.utils.entity;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-04-12
 * Time: 09:51
 */
public class DDUser {

    private String userId;
    private String name;
    private String departName;
    private String job;
    private String mobile;
    private Date hireDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return "DDUser{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", departName='" + departName + '\'' +
                ", job='" + job + '\'' +
                ", mobile='" + mobile + '\'' +
                ", hireDate=" + hireDate +
                '}';
    }
}
