package com.qzh.autosign.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName DateUtil
 * @Author DiangD
 * @Date 2020/4/1
 * @Version 1.0
 * @Description 日期工具类
 **/
public class DateUtil {
    public static String formatByDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
