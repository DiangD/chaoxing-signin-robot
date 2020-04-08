package com.qzh.autosign.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ActiveCourse
 * @Author DiangD
 * @Date 2020/4/7
 * @Version 1.0
 * @Description 成功签到课程封装类
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveCourse {
    private SignedCourse signedCourse;
    private String activeCode;
    private Boolean isActive;
}
