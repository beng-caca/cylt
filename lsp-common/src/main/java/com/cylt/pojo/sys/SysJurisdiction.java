package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 角色
 */
@Component
@Entity
@Table(name = "SYS_JURISDICTION")
@Where(clause = "DEL_STATE = '0'")
@SQLDelete(sql = "UPDATE SYS_JURISDICTION SET DEL_STATE = '1' WHERE id=?", check = ResultCheckStyle.COUNT)
public class SysJurisdiction extends BasePojo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 角色主键
     */
    @Redis
    @Column(name = "ROLE_ID")
    private String roleId;

    /**
     * 菜单主键
     */
    @Redis
    @Column(name = "MENU_ID")
    private String menuId;

    /**
     * 删除标识
     */
    @Redis
    @Column(name = "DEL")
    private Boolean del;

    /**
     * 修改标识
     */
    @Redis
    @Column(name = "EDIT")
    private Boolean edit;


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

    public Boolean getDel() {
        return del;
    }

    public void setDel(Boolean del) {
        this.del = del;
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }
}
