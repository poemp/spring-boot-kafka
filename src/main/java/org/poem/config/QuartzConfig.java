package org.poem.config;

import org.poem.auto.quartz.ProducerQuartz;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author poem
 */
@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail teatQuartzDetail(){
        return JobBuilder.newJob(ProducerQuartz.class).withIdentity("testQuartz").storeDurably().build();
    }

    @Bean
    public Trigger quartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                //设置时间周期单位秒
                .withIntervalInSeconds(1)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(teatQuartzDetail())
                .withIdentity("ProducerQuartz")
                .withSchedule(scheduleBuilder)
                .build();
    }
}