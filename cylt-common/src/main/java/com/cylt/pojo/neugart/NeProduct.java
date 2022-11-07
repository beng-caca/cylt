package com.cylt.pojo.neugart;

import com.cylt.common.LogTitle;
import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品树
 */
@Component
@Entity
@Getter
@Setter
@Table(name = "NE_PRODUCT")
@ApiModel("纽卡特产品树")
@Where(clause = "DEL_STATE = '0'")
@SQLDelete(sql = "UPDATE NE_PRODUCT SET DEL_STATE = '1' WHERE id=?", check = ResultCheckStyle.COUNT)
public class NeProduct extends BasePojo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 菜单父主键
     */
    @Redis
    @ApiModelProperty(value = "父主键")
    @Column(name = "PID")
    private String pid;

    /**
     * 标题
     */
    @Redis
    @ApiModelProperty(value = "标题")
    @Column(name = "TITLE")
    private String title;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Column(name = "ICON")
    private String icon;

    /**
     * 内容
     */
    @LogTitle
    @ApiModelProperty(value = "内容")
    @Column(name = "VALUE")
    private String value;

    /**
     * 计价类型:0=不计价;1=人民币;2=百分比;
     */
    @ApiModelProperty(value = "计价类型:0=不计价;1=人民币;2=百分比;")
    @Column(name = "VALUATION_TYPE")
    private String valuationType;

    /**
     * 计价值 百分比|人民币
     */
    @ApiModelProperty(value = "计价值 百分比|人民币")
    @Column(name = "VALUATION")
    private Double valuation;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @Column(name = "ORDER_BY")
    private Integer orderBy;

    /**
     * 默认配置 1=默认选中;0=非默认选中
     */
    @ApiModelProperty(value = "默认配置 1=默认选中;0=非默认选中")
    @Column(name = "DEFAULT_DATA")
    private Boolean defaultData;

    /**
     * 子菜单
     */
    @ApiModelProperty(value = "子菜单")
    @OneToMany(mappedBy = "pid" ,cascade = { CascadeType.REMOVE})
    private List<NeProduct> childrenList = new ArrayList<>();

    /**
     * 满减
     */
    @ApiModelProperty(value = "满减策略")
    @OneToMany(mappedBy = "productId" , cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("number asc")
    private List<NeProductDiscount> discountList = new ArrayList<>();
}
