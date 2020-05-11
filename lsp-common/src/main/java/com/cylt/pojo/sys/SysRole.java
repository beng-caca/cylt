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
 * 角色
 */
@Component
@Entity
@Table(name = "SYS_ROLE")
@Where(clause = "DEL_STATE = '0'")
@SQLDelete(sql = "UPDATE SYS_ROLE SET DEL_STATE = '1' WHERE id=?", check = ResultCheckStyle.COUNT)
public class SysRole extends BasePojo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 菜单父主键
     */
    @Redis
    @Column(name = "ROLE_NAME")
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
