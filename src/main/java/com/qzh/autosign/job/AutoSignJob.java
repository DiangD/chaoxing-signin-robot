package com.qzh.autosign.job;

import com.qzh.autosign.entity.LoginInfo;
import com.qzh.autosign.entity.SignedCourse;
import com.qzh.autosign.utils.CookieUtil;
import com.qzh.autosign.utils.DateUtil;
import com.qzh.autosign.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName AutoSignJob
 * @Author DiangD
 * @Date 2020/3/31
 * @Version 1.0
 * @Description 定时器执行方法类
 **/
@Slf4j
public class AutoSignJob implements Job {

    private SignedCourse signedCourse;
    private LoginInfo loginInfo;
    private Document document;
    /**
     * 标志签到是否完成
     */
    private boolean isNeedSign = false;

    public void setData(SignedCourse signedCourse, LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
        this.signedCourse = signedCourse;
    }

    public String getActiveId(SignedCourse signedCourse, LoginInfo loginInfo) {
        String signUrl = String.format("https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=%s&jclassId=%s", signedCourse.getCourseId(), signedCourse.getClassId());
        String result;
        String activeId = null;
        try {
            document = Jsoup.connect(signUrl)
                    .userAgent(loginInfo.getUserAgent())
                    .timeout(5000)
                    .cookies(loginInfo.getCookie())
                    .get();
            String content = document.toString();
            if (content.contains("用户登录")) {
                log.debug("cookie过期，重新获取cookie");
                Map<String, String> cookie = CookieUtil.updateCookie(loginInfo.getStudent());
                this.loginInfo.setCookie(cookie);
                getActiveId(signedCourse, loginInfo);
                log.info("cookie已更新");
            } else {
                result = document.select("div#startList > div >div.Mct").attr("onclick");
                if (!result.equals("")) {
                    activeId = result.substring(result.indexOf("(") + 1, result.indexOf(","));
                    isNeedSign = true;
                    log.debug(" 检测到《" + signedCourse.getCourseName() + "》课需要签到");
                } else {
                    log.info(" 检测到《" + signedCourse.getCourseName() + "》暂时不需要签到");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("网络开小差~", e);
        }
        return activeId;
    }

    public void sign() {
        String activeId = getActiveId(signedCourse, loginInfo);
        if ("".equals(activeId) || activeId == null) {
            return;
        }
        String url = String.format("https://mobilelearn.chaoxing.com/widget/sign/pcStuSignController/preSign?activeId=%s&classId=%s&fid=%s&courseId=%s", activeId, signedCourse.getClassId(), loginInfo.getStudent().getFid(), signedCourse.getCourseId());
        String referer = String.format("https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=%s&jclassId=%s", signedCourse.getCourseId(), signedCourse.getClassId());
        try {
            Document document = Jsoup.connect(url)
                    .cookies(loginInfo.getCookie())
                    .header("User-Agent", loginInfo.getUserAgent())
                    .timeout(5000)
                    .header("Referer", referer)
                    .get();
            String result = document.select("span.greenColor").text();
            if ("签到成功".equals(result) && isNeedSign) {
                log.info("签到成功：" + signedCourse.getCourseName());
                sendSignSuccessMsg(signedCourse.getCourseName());
                isNeedSign = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("网络bug了，重试签到：" + signedCourse.getCourseName());
            //sign();
            sendSignFailMsg(signedCourse.getCourseName());
        }
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        AutoSignJob signJob = new AutoSignJob();
        signJob.setData((SignedCourse) dataMap.get("signCourse"), (LoginInfo) dataMap.get("loginInfo"));
        signJob.sign();
    }

    public void sendSignSuccessMsg(String courseName) {
        String title = "《" + courseName + "》签到成功";
        String content = "《%s》于%s完成签到。";
        String format = String.format(content, courseName, DateUtil.formatByDate(new Date()));
        EmailUtil.sendEmail(format, title);
    }

    public void sendSignFailMsg(String courseName) {
        String title = "《" + courseName + "》签到失败";
        String content = "《%s》于%s签到失败，需手动签到，抱歉。";
        String format = String.format(content, courseName, DateUtil.formatByDate(new Date()));
        EmailUtil.sendEmail(format, title);
    }
}
