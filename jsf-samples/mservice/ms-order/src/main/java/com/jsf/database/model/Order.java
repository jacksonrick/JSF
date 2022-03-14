package com.jsf.database.model;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 15:35
 */
public class Order {

    String orderno;
    Integer userId;
    Integer productId;
    Integer money;

    public Order() {
    }

    public Order(String orderno, Integer userId, Integer productId, Integer money) {
        this.orderno = orderno;
        this.userId = userId;
        this.productId = productId;
        this.money = money;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderno='" + orderno + '\'' +
                ", userId=" + userId +
                ", productId=" + productId +
                ", money=" + money +
                '}';
    }
}
