package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单列表
 */
@Component
@Entity
@Table(name = "SYS_MENU")
@Where(clause = "DEL_STATE = '0'")
@SQLDelete(sql = "UPDATE SYS_MENU SET DEL_STATE = '1' WHERE id=?", check = ResultCheckStyle.COUNT)
public class SysMenu extends BasePojo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 菜单父主键
     */
    @Redis
    @Column(name = "PID")
    private String pid;

    /**
     * 菜单名称
     */
    @Redis
    @Column(name = "NAME")
    private String name;

    /**
     * 图标
     */
    @Column(name = "ICON")
    private String icon;

    /**
     * 页面路径
     */
    @Column(name = "COMPONENT")
    private String component;

    /**
     * 是否显示
     */
    @Column(name = "SHOW_MENU")
    private Boolean showMenu;

    /**
     * 子菜单
     */
    @OneToMany(mappedBy = "pid" ,cascade = { CascadeType.REMOVE})
    private List<SysMenu> childrenList = new ArrayList<>();


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Boolean getShowMenu() {
        return showMenu;
    }

    public List<SysMenu> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<SysMenu> childrenList) {
        this.childrenList = childrenList;
    }

    public void setShowMenu(Boolean showMenu) {
        this.showMenu = showMenu;
    }
}
