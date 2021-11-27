package com.hwx.dao.user;

import com.hwx.pojo.Role;
import com.hwx.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 用户接口
 */
public interface UserDao {

     //得到要登录的用户信息
     User getLoginUserInfo(Connection conn,String userCode) throws SQLException;
     //修改密码
     int updatePassword(Connection conn,int id,String newPsd)throws SQLException;
     //根据用户名 或 角色 查询用户总数
     int getUserCounts(Connection conn,String username,int userRole)throws SQLException;
//    //根据条件 查询 获取用户列表 userlist
     List<User> getUserList(Connection conn, String username, int userRole,
                            int currentPageNo, int pageSize)throws SQLException;
     //用户管理模块中的  添加用户
     int addUser(Connection conn,User user)throws SQLException;
     //用户管理模块中的子模块 —— 删除用户
     boolean deleteUser(Connection conn,int userId)throws SQLException;
     //根据用户id 查询用户信息
     User findById(Connection conn,int userId)throws SQLException;
    //用户管理模块中的子 修改用户信息
     boolean modifyUserInfo(Connection conn,int userId,User user)throws SQLException;


}
