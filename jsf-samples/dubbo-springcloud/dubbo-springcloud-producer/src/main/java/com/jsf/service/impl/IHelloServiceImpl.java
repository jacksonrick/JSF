package com.jsf.service.impl;

import com.jsf.service.IHelloService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2019-03-19
 * Time: 12:04
 */
@Service(version = "1.0")
public class IHelloServiceImpl implements IHelloService {

    private static final Logger log = LoggerFactory.getLogger(IHelloServiceImpl.class);

    @Override
    public String hello(String name) {
        log.info("============== hello " + name);
        /*try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return "Hello " + name + "!";
    }

    @Override
    public String hello2() {
        log.info("============== hello2");
        return "HAHA";
    }

}
