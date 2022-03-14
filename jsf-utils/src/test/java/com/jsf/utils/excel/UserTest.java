package com.jsf.utils.excel;

import com.jsf.utils.annotation.excel.Excel;
import com.jsf.utils.annotation.excel.FieldType;
import com.jsf.utils.annotation.excel.Fields;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2019-03-12
 * Time: 15:47
 */
@Excel(name = "测试表格")
public class UserTest {

    @Fields(value = "ID")
    private Integer id;
    @Fields(value = "AA")
    private String A;
    @Fields(value = "BB")
    private String B;
    @Fields(value = "CC")
    private String C;
    @Fields(value = "DD", width = 20)
    private String D;
    @Fields(value = "EE")
    private String E;
    @Fields(value = "FF")
    private String F;
    @Fields(value = "GG", type = FieldType.BOOLEAN)
    private String G;

    public UserTest() {
    }

    public UserTest(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    public String getE() {
        return E;
    }

    public void setE(String e) {
        E = e;
    }

    public String getF() {
        return F;
    }

    public void setF(String f) {
        F = f;
    }

    public String getG() {
        return G;
    }

    public void setG(String g) {
        G = g;
    }

    @Override
    public String toString() {
        return "UserTest{" +
                "id=" + id +
                ", A='" + A + '\'' +
                ", B='" + B + '\'' +
                ", C='" + C + '\'' +
                ", D='" + D + '\'' +
                ", E='" + E + '\'' +
                ", F='" + F + '\'' +
                ", G='" + G + '\'' +
                '}';
    }
}
