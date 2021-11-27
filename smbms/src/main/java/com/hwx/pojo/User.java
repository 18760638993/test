package com.hwx.pojo;/*
 *
 *用户
 */

import java.util.Date;

public class User {
    //id
    private Integer id;
    //用户编码
    private String userCode;
    //用户名
    private String userName;
    //用户密码
    private String userPassword;
    //性别
    private Integer gender;
    //出生日期
    private Date birthday;
    //电话
    private String phone;
    //地址
    private String address;
    //用户角色
    private Integer userRole;
    //创建者
    private Integer createdBy;
    //创建时间
    private Date createDate;
    //更新者
    private Integer modifyBy;
    //更新时间
    private Date modifyDate;
    //年龄
    private Integer age;
    //用户角色名称
    private String userRoleName;

    public Integer getAge() {
        /*Long time = System.currentTimeMillis( ) -birthday. getTime();
        Integer age = Long.valueof(time/365/24/60/60/1ee0).IntegerVaLue();*/

        Date date = new Date();
        Integer age = date.getYear()-birthday.getYear();
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUserRoleName() {
        return userRoleName;
    }

    public void setUserRoleName(String userRoleName) {
        this.userRoleName = userRoleName;
    }





    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(Integer modifyBy) {
        this.modifyBy = modifyBy;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }


}
