package com.jsf;

import com.jsf.lock.RedisLocker;
import com.jsf.model.Admin;
import com.jsf.model.Product;
import com.jsf.service.BatchService;
import com.jsf.service.ProductService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

@SpringBootApplication
@Controller
public class RedisApplication {

    @GetMapping("/")
    @ResponseBody
    public String index(HttpSession session) {
        Admin admin = new Admin();
        admin.setId(1001l);
        admin.setUsername("wang");
        admin.setAge(10);
        admin.setDisabled(false);
        admin.setRoles("SUPER");
        admin.setCreateTime(new Date());
        session.setAttribute("admin", admin);
        return "index";
    }

    @GetMapping("/getuser")
    @ResponseBody
    public Object getuser(HttpSession session) {
        return session.getAttribute("admin");
    }

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/set")
    @ResponseBody
    public String set() {
        stringRedisTemplate.opsForValue().set("spring:uid", "10001");
        stringRedisTemplate.opsForValue().set("spring:name", "wang");

        Product product1 = new Product();
        product1.setId(1001l);
        product1.setName("apple");
        product1.setCurMoney(new BigDecimal("19.9"));
        product1.setDisabled(false);
        product1.setCreateTime(new Date());
        Product product2 = new Product();
        product2.setId(1002l);
        product2.setName("pair");
        product2.setCurMoney(new BigDecimal("99.9"));
        product2.setDisabled(true);
        product2.setCreateTime(new Date());
        redisTemplate.opsForHash().put("spring:hash", "product1", product1);
        redisTemplate.opsForHash().put("spring:hash", "product2", product2);

        redisTemplate.opsForSet().add("spring:set", product1, product2);
        return "success";
    }

    @GetMapping("/get")
    @ResponseBody
    public String get() {
        String uid = (String) stringRedisTemplate.opsForValue().get("spring:uid");
        System.out.println(uid);
        String name = (String) stringRedisTemplate.opsForValue().get("spring:name");
        System.out.println(name);

        Product product = (Product) redisTemplate.opsForHash().get("spring:hash", "product1");
        System.out.println(product);

        Set sets = redisTemplate.opsForSet().members("spring:set");
        System.out.println(sets);
        return "success";
    }


    @Resource
    private ProductService productService;

    @GetMapping("/find")
    @ResponseBody
    public Object find() {
        Product pro = productService.findById3(1001l);
        return pro;
    }

    @GetMapping("/delete")
    @ResponseBody
    public String delete() {
        productService.deleteById(1001l);
        return "success";
    }


    @GetMapping("/logout")
    @ResponseBody
    public String logout(HttpSession session) {
        session.invalidate();
        return "logout success";
    }

    @Resource
    private RedisLocker redisLocker;

    @GetMapping("/rush")
    @ResponseBody
    public String rush(String user) {
        System.out.println(System.currentTimeMillis() + " 用户：" + user + "开始抢购");
        String requestId = UUID.randomUUID().toString();
        boolean result = redisLocker.tryGetDistributedLock("P100", requestId, 10);
        if (result) {
            System.out.println("用户：" + user + "抢到了");
            redisLocker.releaseDistributedLock("P100", requestId);
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }

    @Resource
    private BatchService batchService;

    // 批量插入 178ms
    @GetMapping("/batch/test1")
    @ResponseBody
    public String batchTest1() {
        long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        for (int i = 1; i <= 10000; i++) {
            Product product = new Product();
            product.setId(100000l + i);
            product.setName("apple");
            product.setCurMoney(new BigDecimal("19.9"));
            product.setDisabled(false);
            product.setCreateTime(new Date());
            map.put("key" + i, product);
        }
        batchService.batchSet(map);
        System.out.println("批量插入耗时：" + (System.currentTimeMillis() - start));
        return "SUCCESS";
    }

    // 循环插入 906ms
    @GetMapping("/batch/test2")
    @ResponseBody
    public String batchTest2() {
        long start = System.currentTimeMillis();
        for (int i = 1; i <= 10000; i++) {
            Product product = new Product();
            product.setId(100000l + i);
            product.setName("apple");
            product.setCurMoney(new BigDecimal("19.9"));
            product.setDisabled(false);
            product.setCreateTime(new Date());
            batchService.singleSet("key" + i, product);
        }
        System.out.println("循环插入耗时：" + (System.currentTimeMillis() - start));
        return "SUCCESS";
    }

    @GetMapping("/batch/test3")
    @ResponseBody
    public String batchTest3() {
        Map<String, Object> map = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setId(100l + i);
            product.setName("apple");
            product.setCurMoney(new BigDecimal("19.9"));
            product.setDisabled(false);
            product.setCreateTime(new Date());
            map.put("key" + i, product);
        }
        batchService.batchSetExpire(map, 60);
        return "SUCCESS";
    }

    @GetMapping("/batch/test4")
    @ResponseBody
    public String batchTest4() {
        List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3");
        List<Object> objects = batchService.batchGet(keys);
        System.out.println(objects);
        System.out.println("key1==" + batchService.batchGetMap(keys).get("key2"));
        return "SUCCESS";
    }

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

}
