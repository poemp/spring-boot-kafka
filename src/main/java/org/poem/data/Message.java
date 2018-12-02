package org.poem.data;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @author poem
 */
public class Message<T> {

    /**
     * ID
     */
    private Long id;

    /**
     * 发送的数据
     */
    private T data;

    /**
     * 消息
     */
    private String message;


    /**
     * 发送日期
     */
    private Date sendTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
