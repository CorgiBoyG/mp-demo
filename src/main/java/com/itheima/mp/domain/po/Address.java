package com.itheima.mp.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 虎哥
 * @since 2025-03-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("address")
@ApiModel(value="Address对象", description="")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "县/区")
    private String town;

    @ApiModelProperty(value = "手机")
    private String mobile;

    @ApiModelProperty(value = "详细地址")
    private String street;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "是否是默认 1默认 0否")
    private Boolean isDefault;

    @ApiModelProperty(value = "备注")
    private String notes;

    @ApiModelProperty(value = "逻辑删除")
    private Boolean deleted;


}
