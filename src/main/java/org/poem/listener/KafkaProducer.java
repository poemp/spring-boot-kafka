package org.poem.listener;

import com.alibaba.fastjson.JSONObject;
import org.poem.data.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;


/**
 * @author poem
 */
public class KafkaProducer  implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private String topic;

    private  Message message;

    private KafkaTemplate<String, String> sender;

    /**
     * 数据提交
     * @param topic
     * @param message
     * @param sender
     */
    public KafkaProducer(String topic, Message message, KafkaTemplate<String, String> sender) {
        this.topic = topic;
        this.message = message;
        this.sender = sender;
    }


    /**
     * 发送数据
     */
    @Override
    public void run() {
        logger.info("send topic:" + this.topic);
        logger.info("send data:" + JSONObject.toJSONString(message));
        sender.send(topic, JSONObject.toJSONString(message));
        logger.info("end send success.");
    }
}
