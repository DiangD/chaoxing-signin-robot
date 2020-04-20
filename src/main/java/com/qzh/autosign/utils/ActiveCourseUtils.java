package com.qzh.autosign.utils;

import com.qzh.autosign.entity.ActiveCourse;

/**
 * @ClassName ActivedCourseUtils
 * @Author DiangD
 * @Date 2020/4/7
 * @Version 1.0
 * @Description 用来储存最近一次成功签到的课程信息
 **/
public class ActiveCourseUtils {

    public static ActiveCourse activeCourse = new ActiveCourse();


    public static int getSignType(String title) {
        if (title.contains("手势签到")) {
            return 1;
        } else {
            //默认签到模式
            return 0;
        }
    }
}
