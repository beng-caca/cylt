package com.cylt.pojo.neugart;

import com.cylt.common.LogTitle;
import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * 客户信息
 */
@Component
@Entity
@Getter
@Setter
@Table(name = "NE_CUSTOMER")
@ApiModel("客户信息")
@Where(clause = "DEL_STATE = '0'")
@SQLDelete(sql = "UPDATE NE_CUSTOMER SET DEL_STATE = '1' WHERE id=?", check = ResultCheckStyle.COUNT)
public class NeCustomer extends BasePojo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 登录名
     */
    @Redis
    @LogTitle
    @ApiModelProperty(value = "登录名")
    @Column(name = "LOGIN_ID")
    private String loginId;

    /**
     * 客户名
     */
    @ApiModelProperty(value = "客户名")
    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    /**
     * 客户类型
     */
    @Redis
    @ApiModelProperty(value = "客户类型")
    @Column(name = "CUSTOMER_TYPE")
    private String customerType;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Column(name = "REMAKES")
    private String remakes;
}
