package com.hwx.dao.user;

import com.hwx.dao.BaseDao;
import com.hwx.pojo.Role;
import com.hwx.pojo.User;
import com.mysql.jdbc.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 //登录 判断 的实现类
 */
public class UserDaoImpl implements UserDao{
    @Override
    //得到要登录的用户信息
    public User getLoginUserInfo(Connection conn, String userCode) throws SQLException{
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;
        //如果连数据库都没连接就无需判断了
        if (conn != null) {
            //编写sql语句
            String sql = "select * from smbms_user where userCode=?";
            //存放参数
            Object[] params = {userCode};
            //使用预处理对象调用  操作数据库的公共类 的执行 sql查询语句
            rs = BaseDao.executeQuery(conn, sql, pstm, rs, params);
            //遍历结果集  封装到一个用户中
            if(rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreateDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            //调用  操作数据库的公共类 的执行 释放资源
            BaseDao.closeResource(null,pstm,rs);

        }
        //返回一个用户
        return user;

    }

    @Override
    //修改用户密码
    public int updatePassword(Connection conn, int id, String password) throws SQLException {
        PreparedStatement pstm = null;
        int len=0;//修改影响的数据行数
        //如果连数据库都没连接就无需判断了
        if (conn != null) {
            //编写sql语句
            String sql = "update smbms_user  set userPassword=? where id=?";
            //存放参数,且存放顺序必须和SQL语句中对应！！
            Object[] params = {password,id};
            //使用预处理对象调用  操作数据库的公共类 的执行 sql查询语句
            len= BaseDao.execute(conn, sql, pstm, params);
            //调用  操作数据库的公共类 的执行 释放资源
            BaseDao.closeResource(null,pstm,null);

        }
        //返回结果
        return len;
    }

    @Override
    //根据用户名 或 角色 查询用户总数
    public int getUserCounts(Connection conn, String username, int userRole) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count=0;
        if(conn!=null){
            //SQL语句因为联合多表查询 所以需要拼接
            StringBuffer sql = new StringBuffer();
            //默认两表联合  查询总条数
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole = r.id");
            //建一个集合来存储参数
            ArrayList<Object> list = new ArrayList<>();

            if(!StringUtils.isNullOrEmpty(username)){
                sql.append(" and u.userName like ?");
                list.add("%"+username+"%");//默认下标为0
            }
            if(userRole>0 && userRole<4){
                sql.append(" and u.userRole = ?");
                list.add(userRole);//默认下标为1
            }
            //把list转换为数组
            Object[] parms = list.toArray();

            System.out.println("拼接的sql语句："+sql.toString());
            //执行sql语句
            rs = BaseDao.executeQuery(conn, sql.toString(), pstm, rs,parms );
            //遍历结果集
            if(rs.next()){
                //从结果集中获取数量
                count = rs.getInt("count");
            }
            //最终关闭资源连接
            BaseDao.closeResource(null,pstm,rs);
        }
        return count;
    }


    @Override
    //根据条件 查询 获取用户列表 userlist
    public List<User> getUserList(Connection conn, String username, int userRole, int currentPageNo, int pageSize)
            throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        if(conn!=null){
            //SQL语句因为联合多表查询 所以需要拼接
            StringBuffer sql = new StringBuffer();
            //默认两表联合  查询总条数
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");

            //建一个集合来存储参数
            List<Object> list = new ArrayList<>();
            if(!StringUtils.isNullOrEmpty(username)){
                sql.append(" and u.userName like ?");
                list.add("%"+username+"%");//默认下标为0
            }
            if(userRole>0 && userRole<4){
                sql.append(" and u.userRole = ?");
                list.add(userRole);//默认下标为1
            }
            //在数据库中 分页使用limit startIndex,pageSize 总数
            //当前页 = (当前页-1)*页面大小
            sql.append(" order by u.creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo-1)*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);
            Object[] params = list.toArray();
//            System.out.println("getUserList的语句"+sql.toString());
            //执行sql
            rs = BaseDao.executeQuery(conn, sql.toString(), pstm, rs, params);
            while (rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setUserRoleName(rs.getString("userRoleName"));
                user.setUserRole(rs.getInt("userRole"));
                userList.add(user);
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return userList;
    }

    @Override
    //用户管理模块中的  添加用户操作
    public  int addUser(Connection conn,User user)throws SQLException{
        PreparedStatement pstm = null;
        int updateRows = 0;
        if(conn != null){
            String sql = "insert into smbms_user (userCode,userName,userPassword,gender," +
                    "birthday,phone,address,userRole,createdBy,creationDate)values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params ={user.getUserCode(),user.getUserName(),user.getUserPassword(),user.getGender(),
                    user.getBirthday(),user.getPhone(),user.getAddress(),user.getUserRole(),user.getCreatedBy(),
                    user.getCreateDate()};
            //执行sql 返回执行结果(成功的语句数量)
            updateRows= BaseDao.execute(conn,sql,pstm,params);
            //释放资源
            BaseDao.closeResource(null,pstm,null);
        }
        return updateRows;
    }

    @Override
    //用户管理模块中的 删除用户
    public boolean deleteUser(Connection conn, int userId) throws SQLException {
        PreparedStatement pstm = null;
        boolean flag = false;
        if(conn != null){
            String sql = "delete from smbms_user where id = ?";
            Object[] params = {userId};
            //执行sql 返回执行结果(成功的语句数量)
            int updateRows= BaseDao.execute(conn,sql,pstm,params);
            if(updateRows>0){
                flag = true;
            }
            //释放资源
            BaseDao.closeResource(null,pstm,null);
        }
        return flag;
    }


    @Override
    //根据用户 id 查询用户信息
    public User findById(Connection conn, int userId) throws SQLException {
        User user = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if(conn != null){
            String sql = "select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where" +
                    " u.id = ? and u.userRole = r.id";
            Object[] params ={userId};
            rs = BaseDao.executeQuery(conn, sql, pstm, rs ,params);
            if(rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreateDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
                user.setUserRoleName(rs.getString("userRoleName"));
            }
            //释放资源
            BaseDao.closeResource(null,pstm,rs);
        }
        return user;
    }


    @Override
    //用户管理模块中的  更改用户信息
    public boolean modifyUserInfo(Connection conn, int userId,User user) throws SQLException {
        boolean flag = false;
        PreparedStatement pstm = null;
        int updateRows =0;
        if(conn != null){
            //编写sql语句
            String sql = "update smbms_user set userName = ?,gender = ?,birthday =?,phone = ?," +
                    "address = ?,userRole = ?,modifyBy = ?,modifyDate = ? where id = ?";
            Object[] params = {user.getUserName(),user.getGender(),user.getBirthday(),
                    user.getPhone(),user.getAddress(),user.getUserRole(),user.getModifyBy(),user.getModifyDate(),userId};
            //执行sql语句
            updateRows = BaseDao.execute(conn, sql, pstm, params);
            if(updateRows>0){
                flag = true;
            }
            //释放连接
            BaseDao.closeResource(null,pstm,null);
        }
        return flag;
    }


}
