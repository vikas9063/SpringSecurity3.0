package com.vk.proj.modal;

import jakarta.persistence.*;

@Entity
public class Category {

    @Id
    private String catId;
    private String catName;
    private String catDesc;
    private String catIcon;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;


    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

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

    public Category(String catName, String catDesc, String catIcon,Users user,String catId) {
        this.catName = catName;
        this.catDesc = catDesc;
        this.catIcon = catIcon;
        this.user=user;
        this.catId=catId;
    }

    public Category() {
        super();
    }
}
