package com.hwx.service.user;

import com.hwx.pojo.User;

import java.util.List;

/**
 * 用户登录的业务层
 */
public interface UserService {
    //用户登录
    User login(String userCode, String userPassword);

    //根据用户ID修改密码
    boolean updatePwd(int id, String userPassword);

    //用户管理——查询记录数
    int getUserCounts(String username, int userRole);

    //根据条件 查询用户列表
    List<User> getUserList(String QueryUserName, int QueryUserRole, int currentPageNo,int pageSize);

    //用户管理模块中的  添加用户
    boolean addUser(User user);
    //用户管理模块中的 删除用户
    boolean deleteUserById(int userId);
    //根据id查询用户信息
    User findById(int userId);
    //用户管理模块中的  更改用户信息
    boolean modifyUserInfo(int userId,User user);



}
