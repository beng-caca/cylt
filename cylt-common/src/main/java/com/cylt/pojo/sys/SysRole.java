package com.cylt.pojo.sys;

import com.cylt.common.LogTitle;
import com.cylt.common.Redis;
import com.cylt.common.SysUser;
import com.cylt.common.base.pojo.BasePojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * 角色
 */
@Component
@Entity
@Getter
@Setter
@ApiModel("角色")
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
    @LogTitle
    @ApiModelProperty(value = "角色名称")
    @Column(name = "ROLE_NAME")
    private String roleName;

    /**
     * 用户列表
     */
    @ApiModelProperty(hidden = true)
    @ManyToMany(mappedBy = "roleList")
    private List<SysUser> userList;



    /**
     * 角色权限
     */
    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "roleId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SysJurisdiction> jurisdictionList = new ArrayList<>();

}
