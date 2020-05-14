package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.SysUser;
import com.cylt.common.base.pojo.BasePojo;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * 角色权限表
 */
@Component
@Entity
@Table(name = "SYS_JURISDICTION")
public class SysJurisdiction extends BasePojo {


    /**
     * 角色名称
     */
    @Redis
    @Column(name = "ROLE_ID")
    private String roleId;

    /**
     * 角色名称
     */
    @Redis
    @Column(name = "MENU_ID")
    private String menuId;

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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
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
}
