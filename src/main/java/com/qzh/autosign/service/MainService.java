package com.qzh.autosign.service;

import com.qzh.autosign.entity.LoginInfo;
import com.qzh.autosign.entity.SignedCourse;
import com.qzh.autosign.job.AutoSignJob;
import com.qzh.autosign.utils.EmailUtil;
import com.qzh.autosign.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @ClassName MainService
 * @Author DiangD
 * @Date 2020/3/31
 * @Version 1.0
 * @Description 主服务
 **/
@Slf4j
@Service
public class MainService {
    public void start() throws SchedulerException {
        LoginInfo loginUser = PropertiesUtil.getLoginUser();
        checkLoginStatus(loginUser);

        List<SignedCourse> signedCourses = PropertiesUtil.getCoursesByProperties();
        //创建Scheduler的工厂
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        //从工厂中获取调度器实例
        Scheduler scheduler = schedulerFactory.getScheduler();
        for (SignedCourse signedCourse : signedCourses) {
            try {
                //创建JobDetail(作业信息)
                JobDetail jobDetail = JobBuilder.newJob(AutoSignJob.class)
                        .withDescription("Description:" + signedCourse.getCourseName())
                        .withIdentity("group", signedCourse.getCourseName())
                        .build();
                //向任务传递数据
                JobDataMap dataMap = jobDetail.getJobDataMap();
                dataMap.put("signCourse", signedCourse);
                dataMap.put("loginInfo", loginUser);
                //创建trigger
                Trigger trigger = newTrigger()
                        .withDescription("signTrigger:" + signedCourse.getCourseName())
                        .withIdentity("signTrigger:" + signedCourse.getCourseName(), "signTriggerGroup:" + signedCourse.getCourseName())
                        .withSchedule(CronScheduleBuilder.cronSchedule(signedCourse.getSchedule()))
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.start();
                log.info("已录入课程：" + signedCourse.getCourseName());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("执行时间表有误");
            }
        }
        log.info("所有课程录入成功");
        sendCoursesRecordMsg(signedCourses);
    }

    private void checkLoginStatus(LoginInfo loginInfo) {
        if (loginInfo.getUserAgent().equals("") || loginInfo.getUserAgent() == null) {
            log.error("请配置userAgent，且在联网条件下重新启动");
            System.exit(1);
        }
    }

    private void sendCoursesRecordMsg(List<SignedCourse> signedCourses) {
        String title = "欢迎使用SignInRobot";
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < signedCourses.size(); i++) {
            String course = signedCourses.get(i).getCourseName();
            if (i<signedCourses.size()-1) {
                course = course + "、";
            }
             content.append(course);
        }
        content.append("已成功加入到签到监听队列。");
        EmailUtil.sendEmail(String.valueOf(content), title);
    }
}

