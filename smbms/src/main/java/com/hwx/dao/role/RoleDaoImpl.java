package com.hwx.dao.role;

import com.hwx.dao.BaseDao;
import com.hwx.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @角色接口实现类
 */
public class RoleDaoImpl implements RoleDao{
    @Override
    //获取角色列表
    public List<Role> getRoleList(Connection conn) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Role role=null;
        ArrayList<Role> roleList=new ArrayList<Role>();
        if(conn!=null){
            //编写sql语句
            String sql = "select * from smbms_role";
            //存放参数
            Object[] params = {};
            //使用预处理对象调用  操作数据库的公共类 的执行 sql查询语句
            rs = BaseDao.executeQuery(conn, sql, pstm, rs, params);
            //遍历结果集  封装到一个用户中
            while (rs.next()){
                role = new Role ();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                roleList.add(role);

            }
            //调用  操作数据库的公共类 的执行 释放资源
            BaseDao.closeResource(null,pstm,rs);
        }
        return roleList;
    }

}
