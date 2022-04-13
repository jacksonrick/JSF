package com.jsf.utils.sdk.dingding;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiV2DepartmentListsubRequest;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.jsf.utils.entity.DDUser;
import com.jsf.utils.http.HttpUtils;
import com.jsf.utils.string.StringUtil;
import com.taobao.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 阿里钉钉API
 * User: xujunfei
 * Date: 2022-04-12
 * Time: 09:47
 */
public class DDTalk {

    private static final Logger log = LoggerFactory.getLogger(DDTalk.class);

    private static String TITLE = "XX系统";
    // 钉钉应用配置
    private static String ACCESS_TOKEN_URL = "https://oapi.dingtalk.com/gettoken";
    private static String CORP_ID = "";
    private static String SECRET_ID = "";
    private static Long AGENT_ID = 0L;

    /**
     * 获取所有用户信息
     *
     * @return
     */
    public static List<DDUser> getAllUsers() {
        String accessToken = getAccessToken(ACCESS_TOKEN_URL, CORP_ID, SECRET_ID);
        List<DDUser> users = new ArrayList<>();
        try {
            getAllUsers(accessToken, 1L, users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * 获取所有 部门、用户名、id等信息
     *
     * @param accessToken
     * @param depId
     * @param users
     * @throws Exception
     */
    private static void getAllUsers(String accessToken, Long depId, List<DDUser> users) throws Exception {
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList = getSysDepartmentList(accessToken, depId);
        if (deptList.isEmpty()) {
            return;
        }
        for (OapiV2DepartmentListsubResponse.DeptBaseResponse dept : deptList) {
            log.info("==============> " + dept.getName());
            List<OapiV2UserListResponse.ListUserResponse> respUsers = getDingTalkUserOids(accessToken, dept.getDeptId());
            for (OapiV2UserListResponse.ListUserResponse u : respUsers) {
                DDUser user = new DDUser();
                user.setUserId(u.getUserid());
                user.setDepartName(dept.getName());
                user.setName(u.getName());
                user.setMobile(u.getMobile());
                user.setJob(u.getTitle());
                if (u.getHiredDate() != null) {
                    user.setHireDate(new Date(u.getHiredDate()));
                } else {
                    user.setHireDate(new Date());
                }
                users.add(user);
            }
            getAllUsers(accessToken, dept.getDeptId(), users);
        }
    }

    /**
     * 获取AccessToken
     *
     * @param url
     * @param corpid
     * @param secret
     * @return
     */
    private static String getAccessToken(String url, String corpid, String secret) {
        String requestUrl = url + "?corpid=" + corpid + "&corpsecret=" + secret;
        String result = HttpUtils.doGet(requestUrl, null, null);
        String accessToken = null;
        JSONObject jsonObject = new JSONObject();
        jsonObject = JSON.parseObject(result);
        log.info("获取钉钉AccessToken ====== " + jsonObject);
        String msg = (String) jsonObject.get("errmsg");
        if ("ok".equals(msg)) {
            accessToken = (String) jsonObject.get("access_token");
        }
        return accessToken;
    }

    /**
     * 获取部门列表
     *
     * @param accessToken
     * @param departmentId 1表示顶级部门
     * @return
     * @throws ApiException
     */
    private static List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getSysDepartmentList(String accessToken, Long departmentId) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsub");
        OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
        req.setDeptId(departmentId);
        req.setLanguage("zh_CN");
        OapiV2DepartmentListsubResponse rsp = client.execute(req, accessToken);
        if (rsp.getErrcode() == 0) {
            return rsp.getResult();
        } else {
            log.error(rsp.getErrmsg());
            return Collections.emptyList();
        }
    }

    /**
     * 获取部门下的用户列表
     * >>> 这里获取最多100个（可自行分页）
     *
     * @param accessToken
     * @param departId
     * @return
     * @throws ApiException
     */
    private static List<OapiV2UserListResponse.ListUserResponse> getDingTalkUserOids(String accessToken, Long departId) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
        OapiV2UserListRequest req = new OapiV2UserListRequest();
        req.setDeptId(departId);
        req.setCursor(0L);
        req.setSize(100L); // page_size
        req.setOrderField("modify_desc");
        req.setContainAccessLimit(false);
        req.setLanguage("zh_CN");
        OapiV2UserListResponse rsp = client.execute(req, accessToken);
        if (rsp.getErrcode() == 0) {
            return rsp.getResult().getList();
        } else {
            log.error(rsp.getErrmsg());
            return Collections.emptyList();
        }
    }

    /**
     * 发送消息给用户 - 多个
     *
     * @param UserID  支持多个，使用英文逗号隔开
     * @param Content Markdown格式
     * @return
     */
    public static void sendMessageToUsers(String UserIDs, String Content) {
        if (StringUtil.isBlank(UserIDs)) {
            return;
        }
        String[] users = UserIDs.split(",");
        if (users.length == 0) {
            return;
        }
        String accessToken = getAccessToken(ACCESS_TOKEN_URL, CORP_ID, SECRET_ID);
        for (String userID : users) {
            if (StringUtil.isNotBlank(userID)) {
                sendMessageToUser(userID, Content, accessToken);
            }
        }
    }

    /**
     * 发送消息给用户 - 单个
     *
     * @param UserID
     * @param Content Markdown格式
     */
    public static void sendMessageToUser(String UserID, String Content) {
        if (StringUtil.isBlank(UserID)) {
            return;
        }
        String accessToken = getAccessToken(ACCESS_TOKEN_URL, CORP_ID, SECRET_ID);
        sendMessageToUser(UserID, Content, accessToken);
    }

    /**
     * @param UserID
     * @param Content
     * @param accessToken
     */
    public static void sendMessageToUser(String UserID, String Content, String accessToken) {
        if (StringUtil.isBlank(UserID)) {
            return;
        }
        log.info("钉钉推送======" + UserID + "======" + Content);
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(UserID);
        request.setAgentId(AGENT_ID);
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("markdown");
        msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
        msg.getMarkdown().setTitle(TITLE);
        msg.getMarkdown().setText(Content);
        request.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response response = null;
        try {
            response = client.execute(request, accessToken);
            log.info("钉钉推送======" + response.getErrmsg());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

}
