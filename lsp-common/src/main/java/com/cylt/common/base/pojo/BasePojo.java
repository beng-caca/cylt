package com.cylt.common.base.pojo;


import com.cylt.common.Redis;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
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


    /**
     * 删除标识
     */
    @Column(name = "DEL_STATE")
    private String delState = "0";

    /**
     * 新增人
     */
    @CreatedBy
    @Column(name = "CREATE_BY")
    private String createBy;

    /**
     * 修改人
     */
    @LastModifiedBy
    @Column(name = "UPDATE_BY")
    private String updateBy;

    /**
     * 新增时间
     */
    @Redis
    @Column(name = "CREATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改时间
     */
    @Redis
    @Column(name = "UPDATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    /**
     * 排序条件
     */
    @Transient
    private List<Sort> sort;
}
