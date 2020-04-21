package com.qzh.autosign.utils;

import com.qzh.autosign.entity.ActiveCourse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @ClassName ActivedCourseUtils
 * @Author DiangD
 * @Date 2020/4/7
 * @Version 1.0
 * @Description 用来储存最近一次成功签到的课程信息
 **/
public class ActiveCourseUtils {
    public static final int maxSize = 5;
    private static final HashMap<String, Integer> signTypeMap = new HashMap<String, Integer>(){
        private static final long serialVersionUID = 1L;
        {
            put("签到", 0);
            put("手势签到", 1);
        }
    };


    public static Queue<ActiveCourse> activeCourseQueue  = new LinkedList<>();


    public void addItem(ActiveCourse activeCourse) {
        if (activeCourseQueue.size() >= maxSize) {
            activeCourseQueue.poll();
        }
        activeCourseQueue.offer(activeCourse);
    }


    public boolean isExist(ActiveCourse activeCourse) {
        for (ActiveCourse course : activeCourseQueue) {
            if (course.getActiveCode().equals(activeCourse.getActiveCode())) {
                return true;
            }
        }
        return false;
    }


    public static int getSignType(String title) {
        return signTypeMap.get(title);
    }

}
