package com.cylt.common;

import com.cylt.common.base.pojo.BasePojo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Collection;

/**
 * 用户类
 */
@Component
@Entity
@Table(name = "SYS_USER")
@Where(clause = "DEL_STATE = '0'")
@SQLDelete(sql = "UPDATE SYS_USER SET DEL_STATE = '1' WHERE id=?", check = ResultCheckStyle.COUNT)
public class SysUser extends BasePojo implements UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //登录用户名
    @Redis
    @Column(name = "USER_NAME")
    private String username;
    //登录密码
    @Column(name = "PASSWORD")
    private String password;
    //用户名
    @Redis
    @Column(name = "NAME")
    private String name;
    //企业id
    @Redis
    @Column(name = "ENTERPRISE_ID")
    private String enterpriseId;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

}
