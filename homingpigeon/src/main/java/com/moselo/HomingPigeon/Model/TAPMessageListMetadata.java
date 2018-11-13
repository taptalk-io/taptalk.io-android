package com.moselo.HomingPigeon.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public class TAPMessageListMetadata {
    @JsonProperty("perPage") private Integer perPage;
    @JsonProperty("pageCount") private Integer pageCount;
    @JsonProperty("totalCount") private Integer totalCount;

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
