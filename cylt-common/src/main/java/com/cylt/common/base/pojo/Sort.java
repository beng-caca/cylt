package com.cylt.common.base.pojo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 分页bean
 */
public class Sort {


    /**
     * 是否正序
     */
    @ApiModelProperty(hidden = true)
    private Boolean isAsc;

    /**
     * 排序字段
     */
    @ApiModelProperty(hidden = true)
    private String field;


    public Sort(String field, Boolean isAsc) {
        this.field = field;
        this.isAsc = isAsc;
    }

    public Sort(String field) {
        this(field,true);
    }

    public Boolean getAsc() {
        return isAsc;
    }

    public void setAsc(Boolean asc) {
        isAsc = asc;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
