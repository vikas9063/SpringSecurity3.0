package com.vk.proj.modal;

public class Roles {

    private String roleTitle;
    private String roleDesc;

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

    public Roles(String roleTitle, String roleDesc) {
        this.roleTitle = roleTitle;
        this.roleDesc = roleDesc;
    }
}
