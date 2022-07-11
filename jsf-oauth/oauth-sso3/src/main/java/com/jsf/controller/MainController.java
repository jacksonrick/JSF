package com.jsf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-07-08
 * Time: 16:56
 */
@RestController
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String index(Principal principal) {
        return "登陆成功：" + principal.getName();
    }

    @GetMapping("/unauth")
    public String unauth(String error) {
        log.info("unauth: " + error);
        return "无权限";
    }

    @GetMapping("/api/getInfo")
    public String getInfo() {
        return "get info success";
    }

    // 使用oauthRestTemplate获取资源服务器数据
    @Autowired(required = false)
    @Qualifier("oauthRestTemplate")
    private RestTemplate restTemplate;

    @Value("${spring.security.auth-server}")
    private String authServer;

    @GetMapping("/syncInfo")
    public String syncInfo() {
        String msg = "成功";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username", "ceshi");
            map.add("password", "123");
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
            ResponseEntity<OAuthResponse> result = restTemplate.exchange(authServer + "/api/userSync", HttpMethod.POST, request, OAuthResponse.class);
            OAuthResponse response = result.getBody();
            log.info("OAuth服务器返回：" + response);
            if (response.getCode() != 0) {
                msg = "失败，原因：" + response.getMsg();
            }
        } catch (Exception e) {
            log.error("出错，原因：" + e.getMessage(), e);
            msg = "出错，原因：" + e.getMessage();
        }
        return msg;
    }

    static class OAuthResponse {
        int code;
        String msg;
        Object data;

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
            return "OAuthResponse{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

}
