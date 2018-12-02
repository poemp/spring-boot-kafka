package org.poem.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.poem.data.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author poem
 */
public class KafkaListener {


    private static final Logger logger = LoggerFactory.getLogger(KafkaListener.class);

    /**
     * 线程处理
     */
    private static ThreadPoolTaskExecutor executor;


    /**
     * 初始化之前执行
     */
    @PostConstruct
    private void initExecutor(){
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setMaxPoolSize(100);
        executor.setThreadNamePrefix("KafkaListener-");
        executor.initialize();
    }

    /**
     * 销毁的时候调用
     */
    @PreDestroy
    private void shutShutdownThread() {
        executor.shutdown();
    }

    /**
     * 开启监控
     *
     * @param record
     */
    @org.springframework.kafka.annotation.KafkaListener(topics = {"TEST", "LOGGER"})
    public void listen(ConsumerRecord<String, String> record) {
        logger.info("Kafka-enter:********");
        logger.info("Kafka-enter for record is:" + JSONObject.toJSONString(record.value()));
        String topic = record.topic();
        JSONObject response = JSON.parseObject(record.value());
        Message<Object> message = new Message<>();
         message = JSONObject.parseObject(record.value(), message.getClass());
        logger.info("  响应 ：" + message);


        switch (topic) {
            case "TEST":
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //写日志，发送消息
                        try {
                            logger.info("topic:" + topic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case "LOGGER":
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        logger.info("LOGGER");
                        //写日志，发送消息
                    }
                });
            default:
                break;
        }
    }
}
