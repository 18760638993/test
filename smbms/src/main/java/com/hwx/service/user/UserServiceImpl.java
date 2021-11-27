package com.hwx.service.user;

import com.hwx.dao.BaseDao;
import com.hwx.dao.user.UserDao;
import com.hwx.dao.user.UserDaoImpl;
import com.hwx.pojo.User;
import com.mysql.jdbc.StringUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 用户登录的业务层实现类
 */
public class UserServiceImpl implements UserService{
    //业务层肯定是调用dao层的
    private UserDao userDao;
    public UserServiceImpl(){
        userDao =new UserDaoImpl();
    }

    @Override
    //用户登录
    //(String userCode, String passWord)两个参数对应是的首页传来的值
    public User login(String userCode, String userPassword) {
        //用户登录
        Connection conn = null;
        User user = null;
        try {
            //调用 dao层操作数据库的公共类方法 获取数据库的连接
            conn = BaseDao.getConnection();
            //得到连接后 开始查询 通过业务层调用具体的数据库操作
            user = userDao.getLoginUserInfo(conn, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            BaseDao.closeResource(conn, null, null);
        }
        return user;
    }

    @Override
    //修改密码
    public boolean updatePwd(int id, String userPassword) {
        Connection conn = null;
        boolean flag=false;

        try {
            //调用 dao层操作数据库的公共类方法 获取数据库的连接
            conn = BaseDao.getConnection();
            //密码修改成功
            if (userDao.updatePassword(conn,id,userPassword) >0){
                flag=true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            BaseDao.closeResource(conn, null, null);
        }
        return flag;
    }

    @Override
    //用户管理——查询记录数
    public int getUserCounts(String username, int userRole) {
        Connection conn = null;
        int count=0;
        try {
            //调用 dao层操作数据库的公共类方法 获取数据库的连接
            conn = BaseDao.getConnection();
            count = userDao.getUserCounts(conn,username,userRole);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            BaseDao.closeResource(conn, null, null);
        }
        return count;
    }

    @Override
    //根据条件 查询用户列表
    public List<User> getUserList(String QueryUserName, int QueryUserRole, int currentPageNo,int pageSize) {
        Connection conn = null;
        List<User> userList = null;
        //获取数据库连接
        try {
            conn = BaseDao.getConnection();
            userList = userDao.getUserList(conn, QueryUserName, QueryUserRole, currentPageNo, pageSize);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return userList;
        }
    }

    @Override
    //用户管理模块中的 添加用户
    public boolean addUser(User user) {
        Connection conn = null;
        boolean flag = false;
        int updateRows=0;//改变的行数
        try {
            //获取数据库连接
            conn = BaseDao.getConnection();
            //开启JDBC事务管理
            conn.setAutoCommit(false);
            //Service层调用dao层的方法添加用户
            updateRows = userDao.addUser(conn, user);
            conn.commit();
            if(updateRows > 0){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            }catch (SQLException e1) {
                e1.printStackTrace();
            }
        }finally {
            //释放连接
            BaseDao.closeResource(conn,null,null);

        }
        return flag;
    }


    @Override
    //用户管理模块中 删除用户
    public boolean deleteUserById(int userId) {
        boolean flag = false;
        Connection conn = null;
        try {
            //获取数据库连接
            conn = BaseDao.getConnection();
            //开启事务
            conn.setAutoCommit(false);
            flag = userDao.deleteUser(conn, userId);
            //提交事务
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
            //事务回滚
            conn.rollback();
        }finally {
            //释放连接
            BaseDao.closeResource(conn,null,null);
            return flag;
        }
    }


    @Override
    //根据userId查询用户信息
    public User findById(int userId) {
        User user = null;
        Connection conn = null;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            user = userDao.findById(conn, userId);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }finally {
            BaseDao.closeResource(conn,null,null);

        }
        return user;
    }


    @Override
    //用户管理模块中的  更改用户信息
    public boolean modifyUserInfo(int userId,User user) {
        Connection conn = null;
        boolean flag = false;
        try {
            conn = BaseDao.getConnection();
            //开启事务
            conn.setAutoCommit(false);
            flag = userDao.modifyUserInfo(conn, userId, user);
            //提交事务
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            //事务回滚
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }finally {
            //释放资源
            BaseDao.closeResource(conn,null,null);
        }
        return flag;
    }




    @Test
    public void Test(){
//        UserServiceImpl us = new UserServiceImpl();
//        User user = us.login("admin", "2");
//        System.out.println("用户密码是:"+user.getUserPassword());//admin!=null?true:false
//        System.out.println("是否有该用户:"+user!=null?true:false);//
//        String a="123";
//        System.out.println(StringUtils.isNullOrEmpty(a));
    }
}
