package com.qzh.autosign;

import com.qzh.autosign.utils.ActiveCourseUtils;
import com.qzh.autosign.utils.CookieUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
class AutosignApplicationTests {

    @Test
    void contextLoads() {
        String signUrl = String.format("https://mobilelearn.chaoxing.com/widget/pcpick/stu/index?courseId=%s&jclassId=%s", 211655247,24919535 );
        String cookie = "route=f9c314690d8e5d436efa7770254d0199; uname=1720505246; lv=2; fid=1880; _uid=61405755; uf=b2d2c93beefa90dc1c7d2840b52ee7c533124cc44c5b01508ac06db2400655ac168790e41c4785a22e6591713ee04aa5c49d67c0c30ca5047c5a963e85f11099a607ad0547a39a33ce71fc6e59483dd3ad4c2cfc9b1efcab977d48087cbdb3bd6f7c04307e5fa421; _d=1586424474681; UID=61405755; vc3=Rp%2BDNn5F7ifQMiWj6KVfiWjvUfweQnA8YbI90%2F5rM991qI%2Bbj8OXeXj6sDnIftQntExp7hLMXn6WJ2X2fd3QvzO97ievub%2BxN90EBgleUZNAiGa7HcYSKy8W1Owi9Q4DRzjqpzD%2FMfwgaJoulkhbfymfypFHWDN86xgduJ%2FmC5U%3Df49e24c73666038da96d165a81fdfa0f; xxtenc=81e4c0c0b3bb224c06ca8e88f54a9be2; DSSTASH_LOG=C_38-UN_174-US_61405755-T_1586424474683; source=\"\"; thirdRegist=0; tl=1";
        String result;
        String title;
        String activeId;
        try {
            Document document = Jsoup.connect(signUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
                    .timeout(5000)
                    .cookie("cookie",cookie)
                    .get();
            String content = document.toString();
            if (content.contains("用户登录")) {

            } else {
                //result = document.select("div#startList > div >div.Mct").attr("onclick");
                //Element startList = document.getElementById("startList");
                Elements elements = document.select("div#startList >div");
                for (Element element:elements){
                    System.out.println(element.select(">div.Mct").attr("onclick"));
                    Elements select = element.select(">div.Mct >div.Mct_center>a");
                    System.out.println(select.text());
                }


            }
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

}
