package com.qzh.autosign.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName SignedClass
 * @Author DiangD
 * @Date 2020/3/31
 * @Version 1.0
 * @Description 课程实体
 * course_3=大学生创业基础与实训
 * courseId_3=206719883
 * classId_3=13568791
 * schedule_3=0 10/1 14,18 ? * 4,6
 **/
@Data
@AllArgsConstructor
public class SignedCourse {
    private String courseName;
    private String courseId;
    private String classId;
    private String schedule;
}
