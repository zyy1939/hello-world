package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.system.entity.XiaoTaoVip;
import org.jeecg.modules.system.service.IXiaoTaoVipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 音乐网
 * @Author: zyy
 * @Date: 2022-06-25
 * @Version: V1.0
 */
@Api(tags = "音乐网")
@RestController
@RequestMapping("/xiaoTaoVip")
@Slf4j
public class XiaoTaoVipController extends JeecgController<XiaoTaoVip, IXiaoTaoVipService> {
    @Autowired
    private IXiaoTaoVipService xiaoTaoVipService;

    /**
     * 爬取数据
     */
    @AutoLog(value = "音乐网-爬取数据")
    @ApiOperation(value = "音乐网-爬取数据", notes = "爬取数据")
    @GetMapping(value = "/getXiaoTaoSource")
    public Result<String> getXiaoTaoSource() {
        try {
            this.xiaoTaoVipService.getXiaoTaoSource();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
        return Result.OK("数据爬取中...");
    }

    /**
     * 爬取指定目录
     */
    @AutoLog(value = "音乐网-爬取指定目录")
    @ApiOperation(value = "音乐网-爬取指定目录", notes = "爬取指定目录")
    @GetMapping(value = "/getSourceByUrl")
    public Result<String> getSourceByUrl(String url) {
        try {
            this.xiaoTaoVipService.secondDir(url);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
        return Result.OK("指定URL数据爬取中...");
    }

    /**
     * 分页列表查询
     *
     * @param xiaoTaoVip
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "音乐网-分页列表查询")
    @ApiOperation(value = "音乐网-分页列表查询", notes = "音乐网-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<XiaoTaoVip>> queryPageList(XiaoTaoVip xiaoTaoVip,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        QueryWrapper<XiaoTaoVip> queryWrapper = QueryGenerator.initQueryWrapper(xiaoTaoVip, req.getParameterMap());
        Page<XiaoTaoVip> page = new Page<XiaoTaoVip>(pageNo, pageSize);
        IPage<XiaoTaoVip> pageList = xiaoTaoVipService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param xiaoTaoVip
     * @return
     */
    @AutoLog(value = "音乐网-添加")
    @ApiOperation(value = "音乐网-添加", notes = "音乐网-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody XiaoTaoVip xiaoTaoVip) {
        xiaoTaoVipService.save(xiaoTaoVip);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param xiaoTaoVip
     * @return
     */
    @AutoLog(value = "音乐网-编辑")
    @ApiOperation(value = "音乐网-编辑", notes = "音乐网-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody XiaoTaoVip xiaoTaoVip) {
        xiaoTaoVipService.updateById(xiaoTaoVip);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "音乐网-通过id删除")
    @ApiOperation(value = "音乐网-通过id删除", notes = "音乐网-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        xiaoTaoVipService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "音乐网-批量删除")
    @ApiOperation(value = "音乐网-批量删除", notes = "音乐网-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.xiaoTaoVipService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "音乐网-通过id查询")
    @ApiOperation(value = "音乐网-通过id查询", notes = "音乐网-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<XiaoTaoVip> queryById(@RequestParam(name = "id", required = true) String id) {
        XiaoTaoVip xiaoTaoVip = xiaoTaoVipService.getById(id);
        if (xiaoTaoVip == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(xiaoTaoVip);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param xiaoTaoVip
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XiaoTaoVip xiaoTaoVip) {
        return super.exportXls(request, xiaoTaoVip, XiaoTaoVip.class, "音乐网");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XiaoTaoVip.class);
    }

}
