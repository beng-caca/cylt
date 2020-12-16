package com.cylt.pojo.sys;

import com.cylt.common.LogTitle;
import com.cylt.common.Redis;
import com.cylt.common.SysUser;
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
 * 通知
 */
@Component
@Entity
@Getter
@Setter
@Table(name = "SYS_NOTICE")
@Where(clause = "DEL_STATE = '0'")
@SQLDelete(sql = "UPDATE SYS_NOTICE SET DEL_STATE = '1' WHERE id=?", check = ResultCheckStyle.COUNT)
public class SysNotice extends BasePojo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @Redis(vagueQuery = true)
    @LogTitle
    @Column(name = "CODE")
    private String code;

    /**
     * 标题
     */
    @Redis
    @Column(name = "TITLE")
    private String title;

    /**
     * 通知内容
     */
    @Redis
    @Column(name = "CONTENT")
    private String content;

    /**
     * 回调url
     */
    @Redis
    @Column(name = "CALLBACK_URL")
    private String callbackUrl;

    /**
     * 通知图标路径
     */
    @Column(name = "ICON")
    private String icon;

    /**
     * 跳转参数数据
     */
    @Column(name = "JSON_DATA")
    private String jsonData;

    /**
     * 备注
     */
    @Column(name = "REMAKES")
    private String remakes;

    /**
     * 角色列表
     */
    @ManyToMany
    @JoinTable(name = "SYS_NOTICE_ROLE",joinColumns = @JoinColumn(name = "NOTICE_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private List<SysRole> roleList;
}
