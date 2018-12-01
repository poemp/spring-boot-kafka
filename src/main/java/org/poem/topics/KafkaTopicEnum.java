package org.poem.topics;

public enum KafkaTopicEnum {

    /**
     * 测试
     */
    TEST("TEST"),
    /**
     * 日志
     */
    LOGGER("LOGGER")
    ;

    private String topic;

    KafkaTopicEnum(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
