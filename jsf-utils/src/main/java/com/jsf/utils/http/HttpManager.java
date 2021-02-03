package com.jsf.utils.http;

import com.jsf.utils.system.ExecutorPool;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * Description: Httpclient池
 * User: xujunfei
 * Date: 2021-01-14
 * Time: 14:19
 */
public class HttpManager {

    /**
     * 以下是测试代码，使用单例模式
     * 注意：一个HttpClient对象cookie和context是公用的，如访问多个站点，请生成不同的client
     */

    private HttpManager() {
    }

    public static CloseableHttpClient getClient() {
        return HttpManagerHolder.client;
    }

    private static class HttpManagerHolder {
        static RequestConfig requestConfig;
        static HttpClientConnectionManager connectionManager;

        static {
            RequestConfig.Builder builder = RequestConfig.custom();
            builder.setConnectTimeout(15000); // 设置连接超时时间，单位毫秒
            builder.setSocketTimeout(15000); // 请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
            builder.setCookieSpec(CookieSpecs.STANDARD);
            requestConfig = builder.build();
            // 默认就是PoolingHttpClientConnectionManager
            connectionManager = new PoolingHttpClientConnectionManager();
        }

        // 推荐使用HttpClientBuilder去构建
        private static CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .evictIdleConnections(15, TimeUnit.SECONDS) // 超时关闭空闲连接
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
                .build();

    }

    static AtomicInteger atm = new AtomicInteger(1);

    public static void main(String[] args) {
        Runnable task = (() -> {
            try {
                TimeUnit.SECONDS.sleep(atm.getAndIncrement());
            } catch (InterruptedException e) {
            }
            CloseableHttpClient client = HttpManager.getClient();
            HttpClientContext context = HttpClientContext.create();
            HttpGet get = new HttpGet("https://xxx.com");
            try (CloseableHttpResponse response = client.execute(get, context)) {
                System.out.println(Thread.currentThread().getName() + "==>" + response.getStatusLine().getStatusCode());

                List<Cookie> cookies = context.getCookieStore().getCookies();
                for (Cookie cookie : cookies) {
                    System.out.println(cookie.getName() + "-->" + cookie.getValue());
                }
            } catch (Exception e) {
                System.err.println("error: " + e.getMessage());
            }
        });
        for (int i = 0; i < 5; i++) {
            ExecutorPool.SERVICE.submit(task);
        }
    }
}
