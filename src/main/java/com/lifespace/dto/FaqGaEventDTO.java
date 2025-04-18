package com.lifespace.dto;

public class FaqGaEventDTO {

    private String faqId;        // FAQ 編號
    private String faqTitle;     // FAQ 問題文字
    private long eventCount;     // 點擊次數

    public FaqGaEventDTO() {}

    public FaqGaEventDTO(String faqId, String faqTitle, long eventCount) {
        this.faqId = faqId;
        this.faqTitle = faqTitle;
        this.eventCount = eventCount;
    }

    public String getFaqId() {
        return faqId;
    }

    public void setFaqId(String faqId) {
        this.faqId = faqId;
    }

    public String getFaqTitle() {
        return faqTitle;
    }

    public void setFaqTitle(String faqTitle) {
        this.faqTitle = faqTitle;
    }

    public long getEventCount() {
        return eventCount;
    }

    public void setEventCount(long eventCount) {
        this.eventCount = eventCount;
    }

    @Override
    public String toString() {
        return "FaqGaEventDTO{" +
                "faqId='" + faqId + '\'' +
                ", faqTitle='" + faqTitle + '\'' +
                ", eventCount=" + eventCount +
                '}';
    }
}


