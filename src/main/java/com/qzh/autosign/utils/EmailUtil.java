package com.qzh.autosign.utils;

import lombok.extern.slf4j.Slf4j;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * @ClassName EmailUtil
 * @Author DiangD
 * @Date 2020/4/1
 * @Version 1.0
 * @Description 邮件服务工具类
 **/


@Slf4j
public class EmailUtil {

    private static final String Sender = "***";
    private static final String SendPwd = "***";
    private static final String MAIL_SMTP_HOST = "smtp.163.com";
    private static String receiver = getReceiverEmailAddress();
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    public static String getReceiverEmailAddress() {
        String address = null;
        Properties properties = PropertiesUtil.getInitProperties();
        if (properties.containsKey("email")) {
            address = properties.getProperty("email");
        }
        return address;
    }

    public static void sendEmail(String content, String title) {
        if (receiver != null) {
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.smtp.host", MAIL_SMTP_HOST);//设置邮件服务器主机名
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.setProperty("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.auth", "true");//发送服务器需要身份验证
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            Session session = Session.getDefaultInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Sender, SendPwd);
                }
            });//设置环境信息
            session.setDebug(true);
            try {
                MimeMessage message = createMimeMessage(session, Sender, receiver, content, title);
                Transport.send(message);
            } catch (UnsupportedEncodingException | MessagingException e) {
                e.printStackTrace();
                log.error("邮件发送失败", e);
            }
        } else {
            log.info("没有检测到email配置");
        }
    }

    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail, String content, String title) throws UnsupportedEncodingException, MessagingException {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "签到机器人", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "亲！", "UTF-8"));

        // 4. Subject: 邮件主题
        message.setSubject(title, "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }
}
