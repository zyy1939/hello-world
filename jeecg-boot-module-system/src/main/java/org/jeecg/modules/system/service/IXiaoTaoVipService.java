package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.XiaoTaoVip;

/**
 * @Description: 音乐网
 * @Author: zyy
 * @Date: 2022-06-25
 * @Version: V1.0
 */
public interface IXiaoTaoVipService extends IService<XiaoTaoVip> {

    /**
     * 爬取数据
     */
    void getXiaoTaoSource() throws Exception;

    /**
     * 指定目录查询
     *
     * @param url
     */
    void secondDir(String url) throws Exception;

}
