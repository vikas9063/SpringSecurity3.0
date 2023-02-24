package com.vk.proj.modal;

import jakarta.persistence.*;

@Entity
public class Roles {

    @Id
    private int roleId;

    private String roleTitle;
    private String roleDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;


    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public Roles(String roleTitle, String roleDesc, int roleId,Users user) {
        this.roleTitle = roleTitle;
        this.roleDesc = roleDesc;
        this.roleId=roleId;
        this.user=user;
    }

    public Roles() {
        super();
    }
}
