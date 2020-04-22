package com.qzh.autosign.utils;

import com.qzh.autosign.entity.ActiveCourse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

/**
 * @ClassName ActiveCourseUtils
 * @Author DiangD
 * @Date 2020/4/7
 * @Version 1.0
 * @Description 签到课程工具类
 **/
@Slf4j
public class ActiveCourseUtils {

    /**
     * 队列的最大长度
     */
    public static final int maxSize = setMaxSizeByProperties();
    /**
     * 队列默认的长度
     */
    public static final int QUEUE_DEFAULT_SIZE = 5;

    /**
     * 签到类型与序号的匹配map
     */
    private static final HashMap<String, Integer> signTypeMap = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            put("签到", 0);
            put("手势签到", 1);
        }
    };


    /**
     * 签到课程队列
     */
    public static Queue<ActiveCourse> activeCourseQueue = new LinkedList<>();


    /**
     * @param activeCourse 课程
     * @Description: 向队列尾部添加item，队列长度如果超过maxSize就弹出第一个item（FIFO先进先出）
     */
    public void addItem(ActiveCourse activeCourse) {
        if (activeCourseQueue.size() >= maxSize) {
            activeCourseQueue.poll();
        }
        activeCourseQueue.offer(activeCourse);
    }


    /**
     * @param activeCourse 课程
     * @return 是否存在
     */
    public boolean isExist(ActiveCourse activeCourse) {
        for (ActiveCourse course : activeCourseQueue) {
            if (course.getActiveCode().equals(activeCourse.getActiveCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param title 签到的标题
     * @return 签到类型的序号
     */
    public static int getSignType(String title) {
        return signTypeMap.get(title);
    }

    /**
     * @return 配置文件中的数值
     * @Description: 读取配置文件的最大长度值，并作分析
     */
    private static Integer setMaxSizeByProperties() {
        int size = QUEUE_DEFAULT_SIZE;
        try {
            Properties properties = PropertiesUtil.getInitProperties();
            size = Integer.parseInt(properties.getProperty("courses-queue-maxsize"));
            if (size <= 0) {
                size = QUEUE_DEFAULT_SIZE;
                log.info("队列长度不得小于0，以默认值启动：" + QUEUE_DEFAULT_SIZE);
            }
        } catch (NumberFormatException e) {
            log.warn("配置的格式不正确，应为纯数字，建议介于4和10之间,程序将以默认值启动：" + size, e);
        }
        return size;
    }

}
