package com.coding.tinder.model.request;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description 分页抽象类
 * @ClassName PageRequest
 */
public class PageRequest {

    private int pageNumber = 1;

    private int pageSize = 10;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
