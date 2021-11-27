package com.hwx.dao.role;

import com.hwx.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 角色接口
 */
public interface RoleDao {
    //获取角色列表
    List<Role> getRoleList(Connection conn)throws SQLException;
}
