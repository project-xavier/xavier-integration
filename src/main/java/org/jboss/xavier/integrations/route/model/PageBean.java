package org.jboss.xavier.integrations.route.model;

import java.util.Objects;

public class PageBean {

    private Integer offset;
    private Integer limit;

    public PageBean(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageBean pageBean = (PageBean) o;
        return Objects.equals(offset, pageBean.offset) &&
                Objects.equals(limit, pageBean.limit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, limit);
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
