package org.poem.auto.quartz;

import org.poem.config.SpringUtils;
import org.poem.dao.TopicDao;
import org.poem.data.Message;
import org.poem.executor.Executors;
import org.poem.listener.KafkaProducer;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author poem
 */
public class ProducerQuartz extends QuartzJobBean {


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        TopicDao topicDao = SpringUtils.getBean(TopicDao.class);
        List<String> topics = topicDao.top();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String topic : topics) {
            Message<String> msg = new Message<>();
            msg.setId(10000L);
            msg.setMessage(format.format(new Date()));
            msg.setSendTime(new Date());
            msg.setData("ok");
            Executors.run(new KafkaProducer(topic, msg));
        }

    }
}
