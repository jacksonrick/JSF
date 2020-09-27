package com.jsf;

import com.jsf.utils.encrypt.SignUtil;

import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-09-27
 * Time: 15:57
 */
public class GenSign {

    public static void main(String[] args) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        System.out.println(timestamp);

        TreeMap<String, String> params = new TreeMap<>();
        params.put("timestamp", timestamp);
        //params.put("phone", "13055451873");
        //params.put("password", "5d24bbbe2fe58aba66827aa870004c79");
        String signStr = SignUtil.getSign(params, "u81djiwd33dk");
        System.out.println(signStr);
    }

}
