package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.SysUser;
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
     * 角色名称
     */
    @Redis(vagueQuery = true)
    @Column(name = "ROLE_NAME")
    private String roleName;

    /**
     * 用户列表
     */
    @ManyToMany(mappedBy = "roleList")
    private List<SysUser> userList;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<SysUser> getUserList() {
        return userList;
    }

    public void setUserList(List<SysUser> userList) {
        this.userList = userList;
    }
}
