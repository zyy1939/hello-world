package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
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

    private static Integer COUNT = 0;
    private static final String INDEX_URL = "https://www.xiaotao.vip";

    private static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(32, 32, 60, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(100000));

    @Override
    public void getXiaoTaoSource(Integer totalCount) throws Exception {
        // 初始化总爬取条数
        COUNT = totalCount;

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
            THREAD_POOL.submit(() -> this.secondDir(encode));
//            new Thread(() -> this.secondDir(encode)).start();
        }
    }

    @SneakyThrows
    private void secondDir(String url) {
        try {
            // 延迟访问，防止被宿主机限流
            Thread.sleep(1000L);

            // jsoup抓取页面信息
            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByClass("tr-item");
            for (Element element : elements) {
                // 多线程分析当前页面
                THREAD_POOL.submit(() -> this.saveMusicSource(url, element));
            }
        } catch (Exception e) {
            log.error(url + "_目录查询报错：", e);
            log.error("链接超时，重新爬取:", e.getMessage());
            THREAD_POOL.submit(() -> this.secondDir(url));
        }
    }

    private void saveMusicSource(String url, Element element) {
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
                this.secondDir(url + "/" + element.getElementsByTag("h4").text());
                log.info(element.getElementsByTag("h4").text());
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
                //https://bjbgp01.baidupcs.com/file/
                // 9f8597aba4d51c5c3d1537b25a3b763e
                // ?bkt=en-00f3aa810d089f20a268fcc500048dcb905d90d80f21594c650e7f6175afc7f76495dfccb367242f2cf27cd7b82ca43acfb0104cefc4c67c805dff234697503b
                // &fid=1103004779731-250528-475523598815702
                // &time=1656147384
                // &sign=FDTAXUVbGERQlBHSKfWqi-DCb740ccc5511e5e8fedcff06b081203-VhC9VMrLQUw6OTgyQMcsC8%2BXbz8%3D
                // &to=14
                // &size=1290
                // &sta_dx=1290
                // &sta_cs=6946
                // &sta_ft=m3u
                // &sta_ct=7
                // &sta_mt=4
                // &fm2=MH%2CQingdao%2CAnywhere%2C%2CNone%2Cany
                // &ctime=1484659702
                // &mtime=1653933881
                // &resv0=-1
                // &resv1=0
                // &resv2=rlim
                // &resv3=5
                // &resv4=1290
                // &vuk=1103004779731
                // &iv=2
                // &htype=&randtype=&tkbind_id=0&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=en-7d5c155c42c8e7fea873c9a237a75003b921a603c31e6f6d73522d82c13240f11c07b2180784f4a06e16f7520d1521f225a67a943a2ea2d5305a5e1275657320&expires=8h&rt=pr&r=646512980&vbdid=2874696881&fin=1.群星《流行经典》.m3u&rtype=1&dp-logid=800127504593687646&dp-callid=0.1&tsl=0&csl=0&fsl=-1&csign=K%2FcbvbmVYCjYGl7qKxZ8Fq%2BSiDQ%3D&so=1&ut=1&uter=0&serv=1&uc=1559654663&ti=c77e04c9862927e5db3708ba472a3e60ac5a0c7ecbfa7122305a5e1275657320&hflag=30&from_type=3&adg=c_7ce307483d8c205c118722bbca8535fe&reqlabel=250528_f_d5e10a4c69aebc74908c25e02fd79c3b_-1_dff5d1bfbb2d0f53965b6b259ed68b23&chkv=2&by=themis
                xiaoTaoVip.setDownload(null);
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
//                COUNT--;
//                if (COUNT <= 0) {
//                    throw new IllegalAccessException("每次最多爬取10条数据");
//                }
            }
        } catch (Exception e) {
            log.error("循环体报错：", e);
        }
    }
}
