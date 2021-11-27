package com.hwx.service.role;

import com.hwx.dao.BaseDao;
import com.hwx.dao.role.RoleDao;
import com.hwx.dao.role.RoleDaoImpl;
import com.hwx.pojo.Role;
import com.hwx.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 角色的业务层实现类
 */

public class RoleServiceImpl implements RoleService{
    //引入DAO层
    private RoleDao roleDao;
    public RoleServiceImpl() {
        roleDao =new RoleDaoImpl();
    }

    @Override
    public List<Role> getRoleList() {
        Connection conn = null;
        List<Role> roleList = null;
        //获取数据库连接
        try {
            conn = BaseDao.getConnection();
            roleList = roleDao.getRoleList(conn);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(conn,null,null);
        }
        return roleList;
    }
    @Test
    public void test(){
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList =roleService.getRoleList();
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
//        System.out.println(roleList.toString());

    }

}
