package com.qzh.autosign;

import com.qzh.autosign.service.MainService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutosignApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutosignApplication.class, args);
        try {
            new MainService().start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
