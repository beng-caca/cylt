package com.cylt.common.base.pojo;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页bean
 */
@Getter
@Setter
public class Page<T> {
    /**
     * 第 * 页
     */
    private int pageNumber = 1;

    /**
     * 共 * 条
     */
    private int totalNumber = 0;

    /**
     * 单页 * 条
     */
    private int singlePage = 20;

    /**
     * 分页数据
     */
    private List<T> pageList;
}
