package com.cylt.pojo.sys;

import com.cylt.common.LogTitle;
import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
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
 * 菜单列表
 */
@Component
@Entity
@Getter
@Setter
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
    @LogTitle
    @Column(name = "NAME")
    private String name;

    /**
     * 菜单名称
     */
    @Column(name = "BASE_URL")
    private String baseUrl;

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
}
