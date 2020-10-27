package com.jsf.database.model;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2018-10-31
 * Time: 15:36
 */
public class User {

    private String username;
    private String password;
    private String roles;
    private Boolean disabled;
    private Integer locks;

    public User() {
    }

    public User(User user) {
        this.username = user.username;
        this.password = user.password;
        this.roles = user.roles;
        this.disabled = user.disabled;
        this.locks = user.locks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getLocks() {
        return locks;
    }

    public void setLocks(Integer locks) {
        this.locks = locks;
    }
}
