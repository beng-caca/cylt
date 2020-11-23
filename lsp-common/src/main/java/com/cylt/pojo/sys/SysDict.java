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

/**
 * 字典
 */
@Component
@Entity
@Getter
@Setter
@Table(name = "SYS_DICT")
@Where(clause = "DEL_STATE = '0'")
@SQLDelete(sql = "UPDATE SYS_DICT SET DEL_STATE = '1' WHERE id=?", check = ResultCheckStyle.COUNT)
public class SysDict extends BasePojo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 字典键
     */
    @Redis(vagueQuery = true)
    @Column(name = "DICT_KEY")
    private String dictKey;

    /**
     * 字典值
     */
    @Column(name = "DICT_VALUE")
    private String dictValue;

    /**
     * 字典标题
     */
    @Redis(vagueQuery = true)
    @Column(name = "TITLE")
    private String title;

    /**
     * 字典顺序
     */
    @Redis
    @Column(name = "DICT_ORDER")
    private int dictOrder;


    /**
     * 字典标题
     */
    @Redis(vagueQuery = true)
    @LogTitle
    @Column(name = "REMAKES")
    private String remakes;
}
