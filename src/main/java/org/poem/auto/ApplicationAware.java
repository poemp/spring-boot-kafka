package org.poem.auto;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.poem.dao.TopicDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 监听事物
 *
 * @author poem
 */
@Component
public class ApplicationAware implements ApplicationListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationAware.class);


    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;
    @Value("${spring.kafka.listener.poll-timeout}")
    private String sessionTimeout;
    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private String autoCommitInterval;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${spring.kafka.listener.concurrency}")
    private int concurrency;

    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();


    @Autowired
    private TopicDao topicDao;

    /**
     * 配置
     *
     * @return
     */
    private Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "application-listener");
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return propsMap;
    }


    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

        if (applicationEvent instanceof ApplicationStartedEvent) {
            Consumer<String, String> consumer = new KafkaConsumer<String, String>(consumerConfigs());
            consumer.subscribe(topicDao.top(), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    logger.info(String.format("lost partition in rebalance commting ,current offsets:%s", currentOffsets));
                    //取保在再均衡之前提交当前的偏移量
                    consumer.commitSync(currentOffsets);
                }
            });
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
                    for (ConsumerRecord<String, String> record : records) {
                        logger.info(String.format("topic=%s, partition=%s，offset=%d,custom=%s,country=%s",
                                record.topic(), record.partition(), record.offset(), record.key(), record.value()));
                        currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                                new OffsetAndMetadata(record.offset() + 1, "no metadata"));
                    }
                    consumer.commitAsync(currentOffsets, null);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                try {
                    consumer.commitSync(currentOffsets);
                } finally {
                    logger.info("Close summer");
                    consumer.close();
                }
            }
        }
    }
}
