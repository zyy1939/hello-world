package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 音乐网
 * @Author: zyy
 * @Date:   2022-06-25
 * @Version: V1.0
 */
@Data
@TableName("xiao_tao_vip")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="xiao_tao_vip对象", description="音乐网")
public class XiaoTaoVip implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**歌曲名称*/
	@Excel(name = "歌曲名称", width = 15)
    @ApiModelProperty(value = "歌曲名称")
    private String name;
	/**大小(bit)*/
	@Excel(name = "大小(bit)", width = 15)
    @ApiModelProperty(value = "大小(bit)")
    private Double size;
	/**类型(wav,flac,mp3)*/
	@Excel(name = "类型(wav,flac,mp3)", width = 15)
    @ApiModelProperty(value = "类型(wav,flac,mp3)")
    private String type;
	/**路径*/
	@Excel(name = "路径", width = 15)
    @ApiModelProperty(value = "路径")
    private String path;
	/**下载地址*/
	@Excel(name = "下载地址", width = 15)
    @ApiModelProperty(value = "下载地址")
    private String download;
}
