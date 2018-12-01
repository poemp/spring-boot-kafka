package org.poem.controller;

import org.poem.data.Message;
import org.poem.executor.Executors;
import org.poem.listener.KafkaProducer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author poem
 */
@RestController
public class IndexController {


    @RequestMapping("/send")
    public void send() {
        String message = "hello kafka";
        Message msg = new Message();
        msg.setId(10000L);
        msg.setMsg(message);
        msg.setSendTime(new Date());
        Executors.run(new KafkaProducer("TEST", msg));
    }
}
