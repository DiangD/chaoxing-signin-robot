package com.qzh.autosign.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qzh.autosign.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName CookieUtil
 * @Author DiangD
 * @Date 2020/4/1
 * @Version 1.0
 * @Description 获取cookie的工具类
 **/
@Slf4j
public class CookieUtil {

    public static final String LOGIN_URL = "http://passport2.chaoxing.com/api/login?name=%s&pwd=%s&schoolid=%s&verify=0";

    public static Map<String,String> updateCookie(Student student) {
        Map<String,String> cookieMap = null;
        try {
            String cookie = getCookieOnline(student);
            if (cookie != null) {
                writeCookie(cookie);
                cookieMap = JSON.parseObject(cookie, Map.class);
            } else {
                log.error("cookie更新失败，账号信息错误，重新填写并重启");
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("cookie写入失败");
        }
        return cookieMap;
    }

    public static String getCookieOnline(Student student) throws IOException {
        String url = String.format(LOGIN_URL, student.getStudentNum(), student.getPassword(), student.getFid());
        Connection conn = Jsoup.connect(url).timeout(3000);
        conn.method(Connection.Method.GET);
        conn.followRedirects(false);
        Connection.Response response = conn.execute();
        response.charset("UTF-8");
        Boolean result = (Boolean) JSONObject.parseObject(response.body()).get("result");
        if (result) {
            Map<String, String> cookies = response.cookies();
            return JSON.toJSONString(cookies);
        } else {
            return null;
        }

    }


    private static void writeCookie(String cookie) throws IOException {
        File file = new File("cookie.json");
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (newFile) {
                log.info("成功创建" + file.getName());
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter out = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        out.write(cookie);
        out.flush();
        out.close();
    }
}
