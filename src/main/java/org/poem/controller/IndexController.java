package org.poem.controller;

import org.poem.data.Message;
import org.poem.executor.Executors;
import org.poem.listener.KafkaProducer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author poem
 */
@RestController
@RequestMapping
public class IndexController {


    @ResponseBody
    @RequestMapping("/send")
    public void send() {
        String message = "hello kafka";
        Message<String> msg = new Message<>();
        msg.setId(10000L);
        msg.setMessage(message);
        msg.setSendTime(new Date());
        msg.setData("ok");
        Executors.run(new KafkaProducer("TEST", msg));
    }
}
