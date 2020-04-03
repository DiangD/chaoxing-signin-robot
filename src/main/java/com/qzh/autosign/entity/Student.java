package com.qzh.autosign.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName Student
 * @Author DiangD
 * @Date 2020/4/1
 * @Version 1.0
 * @Description 学生实体
 **/
@Data
@AllArgsConstructor
public class Student {
    //学号
    private String studentNum;
    //密码
    private String password;
    //学校代码
    private String fid;
}
