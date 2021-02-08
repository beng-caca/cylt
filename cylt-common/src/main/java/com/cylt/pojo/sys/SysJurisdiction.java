package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 角色权限
 */
@Component
@Entity
@Getter
@Setter
@ApiModel("角色权限")
@Table(name = "SYS_JURISDICTION")
public class SysJurisdiction extends BasePojo implements GrantedAuthority {


    /**
     * 角色名称
     */
    @Redis
    @ApiModelProperty(value = "角色名称")
    @Column(name = "ROLE_ID")
    private String roleId;

    /**
     * 是否有删除权限
     */
    @ApiModelProperty(value = "是否有删除权限")
    @Column(name = "DEL")
    private boolean del;

    /**
     * 是否有编辑权限
     */
    @ApiModelProperty(value = "是否有编辑权限")
    @Column(name = "EDIT")
    private boolean edit;

    /**
     * 菜单
     */
    @ManyToOne
    @ApiModelProperty(value = "")
    @JoinColumn(name="MENU_ID")
    private SysMenu menu;

    @Override
    public String getAuthority() {
        return null;
    }
}
