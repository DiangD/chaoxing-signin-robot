package com.qzh.autosign.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName LoginInfo
 * @Author DiangD
 * @Date 2020/4/1
 * @Version 1.0
 * @Description 登录需要的信息
 **/
@Data
@AllArgsConstructor
public class LoginInfo {
    private Student student;
    private Map<String,String> cookie;
    private String userAgent;
}
