package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.XiaoTaoVip;
import org.jeecg.modules.system.mapper.XiaoTaoVipMapper;
import org.jeecg.modules.system.service.IXiaoTaoVipService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 音乐网
 * @Author: zyy
 * @Date: 2022-06-25
 * @Version: V1.0
 */
@Slf4j
@Service
public class XiaoTaoVipServiceImpl extends ServiceImpl<XiaoTaoVipMapper, XiaoTaoVip> implements IXiaoTaoVipService {

    private static final String INDEX_URL = "https://www.xiaotao.vip";
    private static final String SECOND_URL = "https://www.xiaotao.vip/?pan_idx=1&path=";

    private static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(32, 32, 60, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(100000));

    @Override
    public void getXiaoTaoSource() {
        new Thread(() -> {
            try {
                this.doMusicSource();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void doMusicSource() throws Exception {
        Document document = Jsoup.connect(INDEX_URL).get();
        Elements elements = document.getElementsByClass("col-4");
        for (Element element : elements) {
            // 网页一级页面
            Elements name = element.getElementsByTag("p");
            log.info(name.text());

            Elements href = element.getElementsByTag("a");
            String encode = URLDecoder.decode(href.get(0).attributes().get("href"), "UTF-8");
            log.info(encode);

            Elements img = element.getElementsByTag("img");
            String imgUrl = INDEX_URL + img.get(0).attributes().get("src");
            log.info(imgUrl);

            // 网页二级目录
            this.secondDir(encode);
        }
    }

    @Override
    public void secondDir(String url) throws Exception {
        try {
            // 延迟访问，防止被宿主机限流
            Thread.sleep(1000L);

            // jsoup抓取页面信息
            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByClass("tr-item");
            for (Element element : elements) {
                // 多线程分析当前页面
                THREAD_POOL.submit(() -> this.saveMusicSource(element));
            }
        } catch (Exception e) {
            log.error(url + "_目录查询报错：", e);
//            log.error("链接超时，重新爬取:", e.getMessage());
//            THREAD_POOL.submit(() -> {
//                try {
//                    this.secondDir(url);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            });
        }
    }

    private void saveMusicSource(Element element) {
        try {
            Elements iTag = element.getElementsByTag("i");
            String aClass = iTag.get(0).attributes().get("class");
            ArrayList<String> classList = Lists.newArrayList(aClass.split(" "));
            if (classList.contains("fa-folder")) {
                // 过滤光良文件夹，路径设计有问题
                if (element.getElementsByTag("h4").text().startsWith("光良")) {
                    log.error("问题文件夹：" + element.getElementsByTag("h4").text());
                    return;
                }
                // 目录
                Elements elementsByClass = element.getElementsByClass("tr-company-name");
                String dataFile = elementsByClass.get(0).attributes().get("data-path");
                dataFile.replace("&#43;", "%20");
                this.secondDir(SECOND_URL + dataFile);
                log.info("文件夹：" + element.getElementsByTag("h4").text());
            } else {
                // 文件
                log.info("名称：" + element.getElementsByTag("h4").text());
                log.info(element.getElementsByTag("p").text());
                Elements elementsByClass = element.getElementsByClass("tr-company-name");
                String dataFile = elementsByClass.get(0).attributes().get("data-file");
                dataFile.replace("&#43;", " ");
                log.info("路径：" + URLDecoder.decode(dataFile, "UTF-8"));

                // 保存文件到数据库
                XiaoTaoVip xiaoTaoVip = new XiaoTaoVip();
                xiaoTaoVip.setName(element.getElementsByTag("h4").text());
                // 文件大小处理
                // <p>大小：26.9M</p>
                // M、K、B
                String sizeString = element.getElementsByTag("p").text();
                String sizeNo = sizeString.split("：")[1];
                String sizeNoString = sizeNo.substring(0, sizeNo.length() - 1);
                Double waitNo = Double.valueOf(sizeNoString);
                String bitType = sizeNo.substring(sizeNo.length() - 1);
                switch (bitType) {
                    case "M":
                        waitNo = waitNo * 1000 * 1000;
                        break;
                    case "K":
                        waitNo = waitNo * 1000;
                        break;
                    case "B":
                    default:
                        break;
                }
                xiaoTaoVip.setSize(waitNo);
                String type = element.getElementsByTag("h4").text();
                String[] split = type.split("\\.");
                xiaoTaoVip.setType(split[split.length - 1]);
                xiaoTaoVip.setPath(URLDecoder.decode(dataFile, "UTF-8"));
                // 下载链接获取
                xiaoTaoVip.setDownload(INDEX_URL + URLDecoder.decode(dataFile, "UTF-8"));
                // 验证歌曲是否存在
                XiaoTaoVip entity = new XiaoTaoVip();
                entity.setName(xiaoTaoVip.getName());
                entity.setSize(xiaoTaoVip.getSize());
                Long selectCount = this.getBaseMapper().selectCount(Wrappers.query(entity));
                if (selectCount > 0) {
                    log.info("歌曲已存在：{},数量：{}", entity.getName(), selectCount);
                    return;
                }

                // 歌曲入库
                this.save(xiaoTaoVip);
            }
        } catch (Exception e) {
            log.error("循环体报错：", e);
        }
    }
}
