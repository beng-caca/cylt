package com.cylt.common.base.pojo;


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
     * <p>Field serialVersionUID: serialVersionUID</p>
     */
    private static final long serialVersionUID = 6630825159500114033L;
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
    @CreatedDate
    @Column(name = "CREATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date createTime;

    /**
     * <p>Field updateTime:修改时间 </p>
     */
    @LastModifiedDate
    @Column(name = "UPDATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
