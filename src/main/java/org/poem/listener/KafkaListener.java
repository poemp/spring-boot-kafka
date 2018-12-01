package org.poem.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author poem
 */
public class KafkaListener {

    @Value("${spring.kafka.bootstrap-servers}")
    private String idmanagerBaseUrl;


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
        executor.setThreadNamePrefix("async-executor-");
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
        String topic = record.topic();
        String taskId = record.key();
        JSONObject response = JSON.parseObject(record.value());
        if (taskId == null) {
            taskId = response.getString("taskId");
        }
        if (taskId == null) {
            logger.error("task id not found,message content is:" + record.value());
            return;
        }
        logger.info("jobId:" + taskId + "  响应 ：" + JSON.toJSONString(record.value()));


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
