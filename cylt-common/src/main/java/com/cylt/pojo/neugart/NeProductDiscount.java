package com.cylt.pojo.neugart;

import com.cylt.common.LogTitle;
import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品折扣策略
 */
@Component
@Entity
@Getter
@Setter
@Table(name = "NE_PRODUCT_DISCOUNT")
@ApiModel("产品折扣策略")
public class NeProductDiscount extends BasePojo {

    /**
     * 产品主键
     */
    @Redis
    @LogTitle
    @ApiModelProperty(value = "产品主键")
    @Column(name = "PRODUCT_ID")
    private String productId;

    /**
     * 客户类型
     */
    @Redis
    @ApiModelProperty(value = "客户类型")
    @Column(name = "CUST_TYPE")
    private String custType;

    /**
     * 折扣值
     */
    @ApiModelProperty(value = "折扣值")
    @Column(name = "DISCOUNT")
    private String discount;

    /**
     * 满减数量
     */
    @ApiModelProperty(value = "满减数量")
    @Column(name = "NUMBER")
    private int number;
}
