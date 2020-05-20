package com.cylt.common.base.pojo;


import com.cylt.common.Redis;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * <p>ClassName: BaseEntity</p>
 * <p>Description: jpa实体基础类</p>
 * <p>Author: wuyh</p>
 * <p>Date: 2020-04-01</p>
 */
@MappedSuperclass
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BasePojo implements java.io.Serializable {

    /**
     * id 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.cylt.common.ManulInsertGenerator")
    private String id;


    //删除标识
    @Column(name = "DEL_STATE")
    private String delState = "0";

    /**
     * <p>Field createBy:新增人 </p>
     */
    @CreatedBy
    @Column(name = "CREATE_BY")
    private String createBy;

    /**
     * <p>Field updateBy:修改人 </p>
     */
    @LastModifiedBy
    @Column(name = "UPDATE_BY")
    private String updateBy;

    /**
     * <p>Field createTime:新增时间 </p>
     */
    @Redis
    @CreatedDate
    @Column(name = "CREATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * <p>Field updateTime:修改时间 </p>
     */
    @Redis
    @LastModifiedDate
    @Column(name = "UPDATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    /**
     * 排序条件
     */
    @Transient
    private List<Sort> sort;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDelState() {
        return delState;
    }

    public void setDelState(String delState) {
        this.delState = delState;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public List<Sort> getSort() {
        return sort;
    }

    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }
}
