package org.poem.executor;

import com.sun.istack.internal.NotNull;
import org.poem.listener.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 线程
 *
 * @author poem
 */
@Component
public class Executors {

    private static Logger logger = LoggerFactory.getLogger(Executors.class);
    /**
     * 线程处理
     */
    private static ThreadPoolTaskExecutor executor= new ThreadPoolTaskExecutor();


    public static void init(){
        logger.info("init executors");
        Executors.executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        Executors.executor.setMaxPoolSize(100);
        Executors.executor.setThreadNamePrefix("kafka-");
        //必须初始化
        //java.lang.IllegalStateException: ThreadPoolTaskExecutor not initialized
        Executors.executor.initialize();
    }
    /**
     * 初始化之前执行
     */
    static {
        init();
    }

    /**
     * 销毁的时候调用
     */
    @PreDestroy
    private void shutShutdownThread() {
        executor.shutdown();
    }

    /**
     * 执行操作
     *
     * @param kafkaProducer
     */
    public static void run(@NotNull KafkaProducer kafkaProducer) {
        Executors.executor.submit(kafkaProducer);
    }
}
