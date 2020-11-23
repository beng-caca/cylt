package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 角色权限表
 */
@Component
@Entity
@Getter
@Setter
@Table(name = "SYS_JURISDICTION")
public class SysJurisdiction extends BasePojo implements GrantedAuthority {


    /**
     * 角色名称
     */
    @Redis
    @Column(name = "ROLE_ID")
    private String roleId;

    /**
     * 是否有删除权限
     */
    @Column(name = "DEL")
    private boolean del;

    /**
     * 是否有编辑权限
     */
    @Column(name = "EDIT")
    private boolean edit;

    /**
     * 菜单
     */
    @ManyToOne
    @JoinColumn(name="MENU_ID")
    private SysMenu menu;

    @Override
    public String getAuthority() {
        return null;
    }
}
