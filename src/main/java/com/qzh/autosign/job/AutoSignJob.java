package com.qzh.autosign.job;

import com.qzh.autosign.entity.ActiveCourse;
import com.qzh.autosign.entity.LoginInfo;
import com.qzh.autosign.entity.Response;
import com.qzh.autosign.entity.SignedCourse;
import com.qzh.autosign.utils.ActiveCourseUtils;
import com.qzh.autosign.utils.CookieUtil;
import com.qzh.autosign.utils.DateUtil;
import com.qzh.autosign.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AutoSignJob
 * @Author DiangD
 * @Date 2020/3/31
 * @Version 1.0
 * @Description 定时器执行方法类
 **/
@Slf4j
public class AutoSignJob extends QuartzJobBean {

    private SignedCourse signedCourse;
    private LoginInfo loginInfo;
    private ActiveCourseUtils activeCourseUtils;
    private List<ActiveCourse> activeCourses;

    /**
     * @param signedCourse 待签到课程信息
     * @param loginInfo    用户的登录信息
     * @Description: 为job内属性赋值
     */
    public void setData(SignedCourse signedCourse, LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
        this.signedCourse = signedCourse;
        activeCourseUtils = new ActiveCourseUtils();
        this.activeCourses = new ArrayList<>();
    }

    /**
     * @param signedCourse 待签到课程信息
     * @param loginInfo    用户的登录信息
     * @Description: 获取签到列表
     */
    public void setActiveCourseList(SignedCourse signedCourse, LoginInfo loginInfo) {
        String signUrl = String.format("https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=%s&jclassId=%s", signedCourse.getCourseId(), signedCourse.getClassId());
        String result;
        String title;
        String activeId = null;
        try {
            Document document = Jsoup.connect(signUrl)
                    .userAgent(loginInfo.getUserAgent())
                    .timeout(5000)
                    .cookies(loginInfo.getCookie())
                    .get();
            String content = document.toString();
            if (content.contains("用户登录")) {
                log.debug("cookie过期，重新获取cookie");
                Map<String, String> cookie = CookieUtil.updateCookie(loginInfo.getStudent());
                this.loginInfo.setCookie(cookie);
                setActiveCourseList(signedCourse, loginInfo);
                log.info("cookie已更新");
            } else {
                Elements elements = document.select("div#startList >div");
                if (elements.size() == 0) {
                    log.info("没有检测到正在进行的签到活动");
                }
                for (Element element : elements) {
                    result = element.select(">div.Mct").attr("onclick");
                    title = element.select(">div.Mct >div.Mct_center>a").text();
                    if (!result.equals("") && title.contains("签到")) {
                        activeId = result.substring(result.indexOf("(") + 1, result.indexOf(","));
                        log.debug(" 检测到《" + signedCourse.getCourseName() + "》课需要签到");
                    }
                    if (!"".equals(activeId) && activeId != null) {
                        activeCourses.add(new ActiveCourse(signedCourse, activeId, false, ActiveCourseUtils.getSignType(title)));
                    } else {
                        log.info(" 检测到《" + signedCourse.getCourseName() + "》暂时不需要签到");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("网络开小差~", e);
        }
    }


    /**
     * 签到流程入口
     */
    public void sign() {
        setActiveCourseList(signedCourse, loginInfo);
        for (ActiveCourse activeCourse : activeCourses) {
            Response response = signByType(activeCourse);
            if (response != null && response.getSuccess()) {
                sendSignSuccessMsg(response);
            }
        }
    }

    /**
     * @param activeCourse 签到课程
     * @return 签到信息封装
     * @Description: 根据签到类型签到
     */
    private Response signByType(ActiveCourse activeCourse) {
        switch (activeCourse.getType()) {
            case 0:
                return normalSign(activeCourse);
            case 1:
                return handSign(activeCourse);
            default:
                break;
            // TODO: 2020/4/9 多种签到方式
        }
        return null;
    }


    /**
     * @param activeCourse 签到课程
     * @return 签到信息封装
     * @Description: 普通签到
     */
    private Response normalSign(ActiveCourse activeCourse) {
        String url = String.format("https://mobilelearn.chaoxing.com/widget/sign/pcStuSignController/preSign?activeId=%s&classId=%s&fid=%s&courseId=%s", activeCourse.getActiveCode(), signedCourse.getClassId(), loginInfo.getStudent().getFid(), signedCourse.getCourseId());
        try {
            boolean isSuccess = isSuccessSignIn(url, activeCourse);
            if (isSuccess) {
                activeCourse.setActive(true);
                activeCourseUtils.addItem(activeCourse);
                log.info("签到成功：" + signedCourse.getCourseName());
                return new Response("签到成功", true, activeCourse.getSignedCourse().getCourseName(), new Date());
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("网络bug了，重试签到：" + signedCourse.getCourseName());
            sendSignFailMsg(new Response("签到出现问题", false, activeCourse.getSignedCourse().getCourseName(), new Date()));
        }
        return null;
    }

    /**
     * @param activeCourse 签到课程
     * @return 签到信息封装
     * @Description: 手势签到
     */
    private Response handSign(ActiveCourse activeCourse) {
        String url = String.format("https://mobilelearn.chaoxing.com/widget/sign/pcStuSignController/signIn?&courseId=%s&classId=%s&activeId=%s", signedCourse.getCourseId(), signedCourse.getClassId(), activeCourse.getActiveCode());

        try {
            boolean isSuccess = isSuccessSignIn(url, activeCourse);
            activeCourse.setActive(true);
            if (isSuccess) {
                activeCourseUtils.addItem(activeCourse);
                log.info("签到成功：" + signedCourse.getCourseName());
                return new Response("签到成功", true, activeCourse.getSignedCourse().getCourseName(), new Date());
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("网络bug了，重试签到：" + signedCourse.getCourseName());
            sendSignFailMsg(new Response("签到出现问题", false, activeCourse.getSignedCourse().getCourseName(), new Date()));
        }
        return null;
    }

    /**
     * @param url          签到连接
     * @param activeCourse 签到课程
     * @return 是否成功
     * @throws IOException io
     * @Description: 判断是否签到成功
     */
    private boolean isSuccessSignIn(String url, ActiveCourse activeCourse) throws IOException {
        String referer = String.format("https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=%s&jclassId=%s", signedCourse.getCourseId(), signedCourse.getClassId());
        Document document = Jsoup.connect(url)
                .cookies(loginInfo.getCookie())
                .header("User-Agent", loginInfo.getUserAgent())
                .timeout(5000)
                .header("Referer", referer)
                .get();
        String result = document.select("span.greenColor").text();
        return "签到成功".equals(result) && !activeCourseUtils.isExist(activeCourse);
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        AutoSignJob signJob = new AutoSignJob();
        signJob.setData((SignedCourse) dataMap.get("signCourse"), (LoginInfo) dataMap.get("loginInfo"));
        signJob.sign();
    }

    /**
     * @param response 签到信息封装
     * @Description: 发送签到成功的邮件
     */
    public void sendSignSuccessMsg(Response response) {
        String title = "《" + response.getCourseName() + "》签到成功";
        String content = "《%s》于%s完成签到。";
        String format = String.format(content, response.getCourseName(), DateUtil.formatByDate(response.getDate()));
        EmailUtil.sendEmail(format, title);
    }

    /**
     * @param response 签到信息封装
     * @Description: 发送签到失败的邮件
     */
    public void sendSignFailMsg(Response response) {
        String title = "《" + response.getCourseName() + "》签到失败";
        String content = "《%s》于%s签到失败，需手动签到，抱歉。";
        String format = String.format(content, response.getCourseName(), DateUtil.formatByDate(response.getDate()));
        EmailUtil.sendEmail(format, title);
    }
}
