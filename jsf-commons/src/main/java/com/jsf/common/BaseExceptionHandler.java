package com.jsf.common;

import com.jsf.database.enums.ResCode;
import com.jsf.system.component.AspectLog;
import com.jsf.utils.annotation.Except;
import com.jsf.utils.entity.ResMsg;
import com.jsf.utils.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 统一异常处理(返回JSON或跳转页面)
 * User: xujunfei
 * Date: 2018-05-23
 * Time: 16:33
 */
@ControllerAdvice
public class BaseExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(BaseExceptionHandler.class);

    /**
     * 异常处理
     *
     * @param e
     * @param request
     * @return page or json
     * @see AspectLog
     * @see SysException
     * @see ApiException
     * @see ApiTokenException
     * @see NoLoginException
     * @see NotAllowException
     * @see AdminNoLoginException
     */
    @ExceptionHandler
    @ResponseBody
    //@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Object exceptionHandler(Throwable e, HttpServletRequest request) {
        TreeMap<String, String> rvs = new TreeMap<String, String>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = (String) paramNames.nextElement();
            String value = request.getParameter(name);
            rvs.put(name, value);
        }
        String s = "Error: {0}, Msg: {1}, Caused by {2}.{3}:{4}, Uri: {5}, Params: {6}";
        s = MessageFormat.format(s,
                e.getClass().getName(),
                e.getMessage(),
                e.getStackTrace()[0].getClassName(),
                e.getStackTrace()[0].getMethodName(),
                e.getStackTrace()[0].getLineNumber(),
                request.getRequestURI(),
                rvs);

        // 如果异常类有`@Unstack`注解，则不输出Stacktrace
        Annotation[] annotations = e.getClass().getDeclaredAnnotations();
        if (annotations.length > 0) {
            // 注意：异常类只能有一个注解，且为Except
            Except except = (Except) annotations[0];
            if (except.stack()) { // 是否打印详细日志
                if (except.error()) { // 是否是Error日志
                    log.error(s, e);
                } else {
                    log.warn(s, e);
                }
            } else {
                if (except.error()) {
                    log.error(s);
                } else {
                    log.warn(s);
                }
            }
        } else { // 无注解默认打印详细日志，且为Error
            log.error(s, e);
        }

        // 自定义异常处理o
        if (e instanceof SysException) { //系统异常
            return new ResMsg(ResCode.ERROR.code(), e.getMessage());
        } else if (e instanceof ApiException) { // api异常
            return new ResMsg(ResCode.APP_ERROR.code(), e.getMessage());
        } else if (e instanceof ApiTokenException) { // api token异常
            return new ResMsg(ResCode.TOKEN_EXP.code(), e.getMessage());
        } else if (e instanceof ConstraintViolationException) { // 接口参数验证异常
            ConstraintViolationException ex = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> cvs = ex.getConstraintViolations();
            for (ConstraintViolation<?> cv : cvs) {
                return new ResMsg(ResCode.CODE_24.code(), cv.getMessage());
            }
            return new ResMsg(ResCode.CODE_24.code(), e.getMessage());
        } else if (e instanceof NoLoginException) { // 未登录
            String requestType = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equalsIgnoreCase(requestType)) { // Ajax
                return new ResMsg(ResCode.NO_LOGIN.code(), ResCode.NO_LOGIN.msg());
            } else { // 如果是页面访问，重定向到登陆页
                String url = request.getRequestURL().toString();
                Map<String, String[]> parameters = request.getParameterMap();
                String para_URL = "";
                try {
                    if (parameters.isEmpty()) {
                        para_URL = URLEncoder.encode(url, "UTF-8");
                    } else {
                        Set<String> keys = parameters.keySet();
                        String _para = "";
                        for (String key : keys) {
                            String[] params = parameters.get(key);
                            _para += "&" + key + "=" + params[0];
                        }
                        para_URL = URLEncoder.encode(url + "?" + _para.substring(1), "UTF-8");
                    }
                } catch (UnsupportedEncodingException ex) {
                    para_URL = "";
                }
                Map map = new HashMap();
                map.put("redirectURL", para_URL);
                return new ModelAndView("/loginPage", map);
            }
        } else if (e instanceof NotAllowException) { // 拒绝访问
            String requestType = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equalsIgnoreCase(requestType)) { // Ajax
                return new ResMsg(ResCode.NOT_ALLOW.code(), ResCode.NOT_ALLOW.msg());
            } else { // Page
                Map map = new HashMap();
                map.put("msg", e.getMessage());
                return new ModelAndView("error/refuse", map);
            }
        } else if (e instanceof AdminNoLoginException) {// admin未登录
            String requestType = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equalsIgnoreCase(requestType)) { // Ajax
                return new ResMsg(ResCode.NO_LOGIN.code(), ResCode.NO_LOGIN.msg());
            } else { // Page
                Map map = new HashMap();
                map.put("msg", e.getMessage());
                return new ModelAndView("/admin/login", map);
            }
        } else {
            // 默认异常处理
            return new ResMsg(ResCode.ERROR.code(), ResCode.ERROR.msg());
        }
        /*if ("XMLHttpRequest".equalsIgnoreCase(requestType)) { // Ajax
            return new ResMsg(ResCode.ERROR.code(), ResCode.ERROR.msg());
        } else { // Page
            Map map = new HashMap();
            map.put("msg", e.getMessage());
            return new ModelAndView("error/500", map);
        }*/
    }

}
