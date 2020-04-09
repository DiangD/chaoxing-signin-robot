package com.qzh.autosign.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName response
 * @Author DiangD
 * @Date 2020/4/9
 * @Version 1.0
 * @Description 签到结果返回封装
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String message;
    private Boolean success;
    private String courseName;
    private Date date;
}
