package com.jsf.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.jsf.common.BaseController;
import com.jsf.database.enums.ResCode;
import com.jsf.service.pay.AliPayService;
import com.jsf.service.pay.WxPayService;
import com.jsf.system.conf.SysConfig;
import com.jsf.utils.entity.ResMsg;
import com.jsf.utils.file.Qrcode;
import com.jsf.utils.sdk.wxpay.WXPay;
import com.jsf.utils.sdk.wxpay.WXPayUtil;
import com.jsf.utils.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付接口
 * Alipay & Wechat
 * Created by xujunfei on 2017/3/25.
 */
@Controller
@RequestMapping("/pay")
public class AppPayController extends BaseController {

    private static Logger log = LoggerFactory.getLogger(AppPayController.class);

    @Resource
    private AliPayService aliPayService;
    @Resource
    private WxPayService wxPayService;

    //*******************************支付宝*******************************//

    /**
     * 支付宝扫码
     *
     * @return
     */
    @GetMapping("/qr/alipay")
    public String alipayQr() {
        return "pay/alipay_qr";
    }

    /**
     * 支付宝二维码支付
     *
     * @param response
     * @throws Exception
     */
    @GetMapping("/qrcode/alipay")
    public void alipayQrcode(HttpServletResponse response) throws Exception {
        String result = aliPayService.qrcode("商品", 0.01, StringUtil.getOrderCode());
        JSONObject object = JSON.parseObject(result).getJSONObject("alipay_trade_precreate_response");
        if (object.get("value") != null) {
            System.out.println(object.get("sub_msg"));
            return;
        }
        String qr = object.getString("qr_code");
        // 生成二维码
        Qrcode.write(qr, response);
    }

    /**
     * 支付宝网页支付
     * <p>直接回跳转到支付宝网关</p>
     *
     * @return
     */
    @GetMapping("/pc/alipay")
    @ResponseBody
    public String alipay() throws Exception {
        String result = aliPayService.alipayWeb("测试", "商品", 100d, StringUtil.getOrderCode());
        System.out.println(result);
        return result;
    }

    /**
     * 网页支付成功跳转页面
     *
     * @return
     */
    @GetMapping("/page/success")
    public String alipayReturn() {
        return "pay/success";
    }

    /**
     * 支付宝退款
     *
     * @return
     */
    @GetMapping("/refund/alipay")
    @ResponseBody
    public ResMsg alipayRefund() throws Exception {
        String result = aliPayService.refund("2018011711333198018150", "2018011721001004630200410967", 100d, "退款");
        return new ResMsg(ResCode.SUCCESS.code(), ResCode.SUCCESS.msg(), result);
    }


    /**
     * 支付宝 Callback
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @PostMapping("/callback/alipay")
    public void alipayCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("接收到支付宝回调");
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            if ("biz".equals(name)) {
                continue;
            }
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决
            valueStr = new String(valueStr.getBytes(), "utf-8");
            params.put(name, valueStr);
        }
        boolean flag = AlipaySignature.rsaCheckV1(params, SysConfig.get("alipay.publicKey"), "utf-8", "RSA2");
        log.info("flag:" + flag + ", param:" + params);
        if (flag) {
            String biz = request.getParameter("passback_params"); // 自定义业务码
            if (StringUtil.isBlank(biz)) {
                biz = request.getParameter("biz");
            }

            // passback_params | biz
            // trade_no
            // out_trade_no
            // trade_status     TRADE_SUCCESS
            // total_amount

            // aliPayService.callback(params);  // 处理业务
            output(response, "success");
        } else {
            output(response, "failure");
        }
    }

    //*******************************微信*******************************//

    /**
     * 微信二维码支付页面
     *
     * @return
     */
    @GetMapping("/qr/wx")
    public String wxQr() {
        return "pay/wx_qr";
    }

    /**
     * 生成支付二维码
     *
     * @param response
     * @throws Exception
     */
    @GetMapping("/qrcode/wx")
    public void wxQrcode(HttpServletResponse response) throws Exception {
        String code_url = wxPayService.orderQrcode(StringUtil.getOrderCode(), "商品", 0.01, "114.114.114.114");
        // 生成二维码
        Qrcode.write(code_url, response);
    }

    /**
     * 微信退款
     *
     * @return
     */
    @GetMapping("/refund/wx")
    @ResponseBody
    public ResMsg wxRefund() throws Exception {
        wxPayService.refund("2018012515533680990396", StringUtil.getOrderCode(), 0.01, 0.01);
        return new ResMsg(ResCode.SUCCESS.code(), ResCode.SUCCESS.msg());
    }

    /**
     * 微信支付 Callback
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @PostMapping("/callback/wxpay")
    public void wxpayCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("接收到微信支付回调");
        // 读取微信回调数据
        DataInputStream in = new DataInputStream(request.getInputStream());
        byte[] dataOrigin = new byte[request.getContentLength()];
        in.readFully(dataOrigin);
        if (null != in) in.close();
        String notifyData = new String(dataOrigin); // 支付结果通知的xml格式数据
        log.info("notifyData:" + notifyData);

        WXPay wxpay = new WXPay(wxPayService);
        Map<String, String> params = WXPayUtil.xmlToMap(notifyData);  // 转换成map
        if (wxpay.isPayResultNotifySignatureValid(params)) {
            System.out.println("签名正确，参数：" + params);
            // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功

            // result_code      SUCCESS
            // out_trade_no
            // transaction_id
            // total_fee
            // attach           自定义业务码

            // wechatPayService.callback(params);  // 处理业务
            output(response, output("SUCCESS", "OK"));
        } else {
            log.info("签名错误");
            output(response, output("FAIL", "FAIL"));
        }
    }


    private void output(HttpServletResponse response, String ret) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {

        }
        out.println(ret);
        out.flush();
        out.close();
    }

    private String output(String code, String message) {
        return "<xml> \n" +
                "  <return_code><![CDATA[" + code + "]]></return_code>\n" +
                "  <return_msg><![CDATA[" + message + "]]></return_msg>\n" +
                "</xml>";
    }
}
