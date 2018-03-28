package com.motivtelecom.dao;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class SAPCObject {
    private BigDecimal fid;
    private BigDecimal priority;
    private Boolean entryfound = false;

    public BigDecimal getFid() {
        return fid;
    }

    void setFid(BigDecimal fid) {
        this.fid = fid;
    }

    public BigDecimal getPriority() {
        return priority;
    }

    void setPriority(BigDecimal priority) {
        this.priority = priority;
    }

    public Boolean getEntryfound() {
        return entryfound;
    }

    public void setEntryfound(Boolean entryfound) {
        this.entryfound = entryfound;
    }
}
