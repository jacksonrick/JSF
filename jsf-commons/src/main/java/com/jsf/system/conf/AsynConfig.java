package com.jsf.system.conf;

import com.jsf.utils.system.LogManager;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created with IntelliJ IDEA.
 * Description: 异步任务配置 需要启用注解@EnableAsync
 * User: xujunfei
 * Date: 2017-11-28
 * Time: 11:03
 */
@ConfigurationProperties(prefix = "spring.task")
public class AsynConfig implements AsyncConfigurer {
    // @EnableAspectJAutoProxy(exposeProxy = true) // 解决异步方法同步调用的问题
    // XXService service = (XXService) AopContext.currentProxy(); service.async();

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int keepAliveSeconds;
    private boolean waitOnShutdown;
    private int awaitTerminationSeconds;

    @Override
    public Executor getAsyncExecutor() {
        LogManager.info("initializing custom asyncExecutor configuration...", getClass());
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setWaitForTasksToCompleteOnShutdown(waitOnShutdown);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        // 任务前缀
        executor.setThreadNamePrefix("asyncTaskExecutor-");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable arg0, Method arg1, Object... arg2) {
                LogManager.error("AsyncTaskExecutePool Exception Method:" + arg1.getName(), arg0);
            }
        };
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public boolean isWaitOnShutdown() {
        return waitOnShutdown;
    }

    public void setWaitOnShutdown(boolean waitOnShutdown) {
        this.waitOnShutdown = waitOnShutdown;
    }

    public int getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }
}
