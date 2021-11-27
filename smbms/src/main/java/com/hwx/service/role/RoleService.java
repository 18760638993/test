package com.hwx.service.role;


import com.hwx.pojo.Role;

import java.util.List;

/**
 * 角色业务层接口
 */
public interface RoleService {
    //根据条件 查询用户列表
    List<Role> getRoleList();


}
