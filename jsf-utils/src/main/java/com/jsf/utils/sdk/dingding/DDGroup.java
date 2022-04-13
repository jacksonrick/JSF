package com.jsf.utils.sdk.dingding;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsf.utils.string.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-04-12
 * Time: 10:27
 */
public class DDGroup {

    private static final Logger log = LoggerFactory.getLogger(DDGroup.class);

    private static String TITLE = "XX系统";

    // 钉钉机器人URL
    private static String GROUP_ROBOT_WEBHOOK_URL = "https://oapi.dingtalk.com/robot/send?access_token=";


    /**
     * 发送消息到组
     *
     * @param content
     * @param cfg
     * @param phones  at手机号
     * @param names   at姓名（与手机号一一对应）
     */
    public static void sendToGroup(String content, String cfg, JSONArray phones, JSONArray names) {
        if (StringUtil.isBlank(cfg)) {
            return;
        }
        log.info("钉钉群组推送======" + content);
        String[] ss = cfg.split(";");
        sendToGroup(content, ss[0], ss[1], phones, names);
    }

    /**
     * 发送消息到组
     * <p>
     * 加粗/斜体： ****  **
     * 换行：  \n
     * 链接：[描述](http://xxx/xx)
     * 图片：![screenshot](http://img.xxx.png)
     * </p>
     *
     * @param phones
     * @param names
     */
    public static void sendToGroup(String content, String accessToken, String secret, JSONArray phones, JSONArray names) {
        if (accessToken.length() < 10) {
            return;
        }
        CloseableHttpClient client = null;
        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            //System.out.println(sign);
            String hookUrl = GROUP_ROBOT_WEBHOOK_URL + accessToken + "&timestamp=" + timestamp + "&sign=" + sign;
            //System.out.println(hookUrl);

            //msgtype
            JSONObject object = new JSONObject();
            object.put("msgtype", "markdown");

            //markdown
            Map map = new HashMap();
            map.put("title", TITLE);
            map.put("text", content);
            object.put("markdown", map);

            //at
            Map map2 = new HashMap();
            map2.put("isAtAll", false);
            map2.put("atMobiles", phones);
            map2.put("atUserIds", names);
            object.put("at", map2);

            client = HttpClients.createDefault();

            HttpPost post = new HttpPost(hookUrl);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
            post.setConfig(requestConfig);
            post.setHeader("Content-Type", "application/json");
            StringEntity paramEntity = new StringEntity(object.toString(), "UTF-8");
            post.setEntity(paramEntity);
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String result = "";
            if (entity != null) {
                result = EntityUtils.toString(entity, "GB2312");
            }
            log.info("钉钉群组推送响应======" + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    ((CloseableHttpClient) client).close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

}
