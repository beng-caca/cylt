package com.cylt.common.base.pojo;


import java.util.List;

/**
 * 分页bean
 */
public class Page {
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
    private List pageList;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public int getSinglePage() {
        return singlePage;
    }

    public void setSinglePage(int singlePage) {
        this.singlePage = singlePage;
    }

    public List getPageList() {
        return pageList;
    }

    public void setPageList(List pageList) {
        this.pageList = pageList;
    }
}
