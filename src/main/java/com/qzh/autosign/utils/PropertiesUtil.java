package com.qzh.autosign.utils;

import com.qzh.autosign.entity.LoginInfo;
import com.qzh.autosign.entity.SignedCourse;
import com.qzh.autosign.entity.Student;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName PropertiesUtil
 * @Author DiangD
 * @Date 2020/3/31
 * @Version 1.0
 * @Description 配置文件工具类
 **/
@Component
public class PropertiesUtil {


    public static final String DEFAULT_URI = "init.properties";

    public static LoginInfo getLoginUser() {
        Properties properties = getInitProperties();
        String studentNum = properties.getProperty("studentNum");
        String password = properties.getProperty("password");
        String fid = properties.getProperty("fid");
        Student student = new Student(studentNum, password, fid);
        String userAgent = properties.getProperty("userAgent");
        Map<String, String> cookie = CookieUtil.updateCookie(student);
        return new LoginInfo(student, cookie, userAgent);
    }

    public static List<SignedCourse> getCoursesByProperties() {
        LinkedList<SignedCourse> signedCourses = new LinkedList<>();
        Properties properties = getInitProperties();
        int i = 1;
        while (properties.getProperty("course_" + i) != null) {
            if (properties.getProperty("course_" + i) != null) {
                SignedCourse signedCourse = new SignedCourse(properties.getProperty("course_" + i), properties.getProperty("courseId_" + i), properties.getProperty("classId_" + i), properties.getProperty("schedule_" + i));
                signedCourses.add(signedCourse);
            }
            i++;
        }
        return signedCourses;
    }

    public static Properties getInitProperties() {
        Properties properties = new Properties();
        File file = new File(DEFAULT_URI);
        if (file.exists()) {
            try {
                properties.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

}
