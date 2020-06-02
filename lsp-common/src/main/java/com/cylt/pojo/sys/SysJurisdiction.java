package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 角色权限表
 */
@Component
@Entity
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public boolean getDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public boolean getEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public SysMenu getMenu() {
        return menu;
    }

    public void setMenu(SysMenu menu) {
        this.menu = menu;
    }

    @Override
    public String getAuthority() {
        return null;
    }
}
