package com.vk.proj.modal;

public class Expense {

    private String expId;
    private String expTitle;
    private String expCategory;
    private String expOn;

    private int expOnYear;
    private int expOnMonth;
    private int expOnDate;
    private String userId;
    private String expDesc;

    private Double price;

    public int getExpOnDate() {
        return expOnDate;
    }

    public void setExpOnDate(int expOnDate) {
        this.expOnDate = expOnDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public String getExpTitle() {
        return expTitle;
    }

    public void setExpTitle(String expTitle) {
        this.expTitle = expTitle;
    }

    public String getExpCategory() {
        return expCategory;
    }

    public void setExpCategory(String expCategory) {
        this.expCategory = expCategory;
    }

    public String getExpOn() {
        return expOn;
    }

    public void setExpOn(String expOn) {
        this.expOn = expOn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExpDesc() {
        return expDesc;
    }

    public void setExpDesc(String expDesc) {
        this.expDesc = expDesc;
    }

    public int getExpOnYear() {
        return expOnYear;
    }

    public void setExpOnYear(int expOnYear) {
        this.expOnYear = expOnYear;
    }

    public int getExpOnMonth() {
        return expOnMonth;
    }

    public void setExpOnMonth(int expOnMonth) {
        this.expOnMonth = expOnMonth;
    }
}
