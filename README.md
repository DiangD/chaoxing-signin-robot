# 基于java实现的超星签到助手
参考自https://github.com/sunjulei/chaoxing-sign
多次因为睡懒觉错过签到，萌生了这种邪恶的死宅想法。
## 目前功能
* 基于学号、密码、机构的登录机制
* 自动创建以及更新cookie
* 经测试，目前适应于普通登录。手势登录未知、刷脸登录未实现
* 通过cron表达式控制执行时间
* 邮件通知功能
## 部署方法
* JDK 1.8及以上
* jar包部署 java - jar **
* 配置好init.properties(必须包含学生信息、教程信息、邮箱地址)
* 邮箱基于stmp服务，需要一个开通服务的mail（修改源码）
* 收到邮件通知即程序成功启动（或者查看log）

## 更进一步
1. 可视化界面
2. 动态获取课程信息，写入到配置文件
3. 启动方式改进（exe、bat）
4. 多种登陆方式
5. 对多种签到方法的支持

有空再说，下次一定！！！

## init。properties模板
```properties
#userAgent（浏览器标识）
userAgent=Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36

#账号信息（学号、密码、学校id）
studentNum=****
password=****
fid=****

#按照以下格式添加课程，从一开始递增(课程名字、courseId、classId、cron表达式)
course_1=Internet与Web技术
courseId_1=208815981
classId_1=17990784
schedule_1=0 /1 8-12 ? * FRI

course_2=游戏引擎原理与应用实验
courseId_2=208229909
classId_2=17330704
schedule_2=0 /1 18-21 ? * MON

course_3=游戏引擎原理与应用
courseId_3=208229909
classId_3=17330704
schedule_3=0 /1 8-11 ? * TUES

course_4=影视后期制作
courseId_4=208332815
classId_4=20065904
schedule_4=0 /1 14-17 ? * TUES

course_5=影视后期制作实验
courseId_5=208332815
classId_5=20065904
schedule_5=0 /1 19-22 ? * TUES

course_6=视听语言实验
courseId_6=208566119
classId_6=17531582
schedule_6=0 /1 8-11 ? * WED

course_7=视听语言
courseId_7=208093551
classId_7=16363609
schedule_7=0 /1 13-15 ? * WED

#配置接收邮箱
email=******

#每次修改配置文件都要重启程序！！！！！

```



