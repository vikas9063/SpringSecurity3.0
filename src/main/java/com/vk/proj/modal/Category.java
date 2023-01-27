package com.vk.proj.modal;

public class Category {

    private String catName;
    private String catDesc;
    private String catIcon;

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setCatDesc(String catDesc) {
        this.catDesc = catDesc;
    }

    public void setCatIcon(String catIcon) {
        this.catIcon = catIcon;
    }

    public String getCatName() {
        return catName;
    }

    public String getCatDesc() {
        return catDesc;
    }

    public String getCatIcon() {
        return catIcon;
    }

    public Category(String catName, String catDesc, String catIcon) {
        this.catName = catName;
        this.catDesc = catDesc;
        this.catIcon = catIcon;
    }
}
