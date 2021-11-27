package com.hwx.dao.provider;

import com.hwx.dao.BaseDao;
import com.hwx.pojo.Provider;
import com.mysql.jdbc.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 供应商接口实现类
 */
public class ProviderDaoImpl implements ProviderDao {
    @Override
    //查询供应商列表数据
    public List<Provider> getProviderInfo(Connection conn, String proCode, String proName, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Provider> providerList = new ArrayList<Provider>();
        if (conn != null) {
            //供应商无需连表查询 sql需拼接参数
            StringBuffer sql = new StringBuffer();
            sql.append("select * from smbms_provider");
            //建一个参数列表 注意这里泛型使用Object
            List<Object> paramsList = new ArrayList<>();
            //SQL语句拼接  参数赋值
            // 如果两个都不为空 sql需要拼接and
            if (proCode != "" && proName != "") {
                sql.append(" where ProCode like ? and ProName like ?");
                paramsList.add("%" + proCode + "%");
                paramsList.add("%" + proName + "%");
            } else if (proCode != "" || proName != "") {
                sql.append(" where");
                if (proCode != "") {
                    sql.append(" ProCode like ?");
                    paramsList.add("%" + proCode + "%");
                }
                if (proName != "") {
                    sql.append(" ProName like ?");
                    paramsList.add("%" + proName + "%");
                }
            }
            System.out.println(sql.toString());
            //在数据库中 分页使用limit startIndex,pageSize 总数
            //当前页 = (当前页-1)*页面大小
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            paramsList.add(currentPageNo);
            paramsList.add(pageSize);

            //将参数列表进行转换
            Object[] params = paramsList.toArray();
            System.out.println("Test getProviderList ->  " + sql);
            //执行sql
            rs = BaseDao.executeQuery(conn, sql.toString(), pstm, rs, params);
            //遍历结果集 封装到一个供应商中
            while (rs.next()) {
                Provider provider = new Provider();
                provider.setId(rs.getInt("id"));
                provider.setProCode(rs.getString("proCode"));
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
                provider.setCreatedBy(rs.getInt("createdBy"));
                provider.setCreationDate(rs.getTimestamp("creationDate"));
                provider.setModifyDate(rs.getTimestamp("modifyDate"));
                provider.setModifyBy(rs.getInt("modifyBy"));
                //将此供应商信息添加至 列表
                providerList.add(provider);
            }
            //释放资源
            BaseDao.closeResource(null, pstm, rs);

        }
        return providerList;
    }

    @Override
    //根据供应商编码 或 供应商名称 查询供应商总数
    public int getProviderCounts(Connection conn, String proCode, String proName) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int providerCounts = 0;
        if (conn != null) {
            //使用字符拼接的方式生成sql语句
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as providerCount from smbms_provider");
            //参数列表
            ArrayList<Object> paramsList = new ArrayList<>();
            //判断是否有2个参数 如果有就得加and
            //判断是否有2个参数 如果有就得加and
            if (proCode != "" && proName != "") {
                sql.append(" where ProCode like ? and ProName like ?");
                paramsList.add("%" + proCode + "%");
                paramsList.add("%" + proName + "%");
            } else if (proCode != "" || proName != "") {
                sql.append(" where");
                if (proCode != "") {
                    sql.append(" ProCode like ?");
                    paramsList.add("%" + proCode + "%");
                }
                if (proName != "") {
                    sql.append(" ProName like ?");
                    paramsList.add("%" + proName + "%");
                }
            }
            //sql拼接完成
            System.out.println("Test ProviderDaoImpl -> " + sql.toString());
            //参数列表转换
            Object[] params = paramsList.toArray();
            //执行sql
            rs = BaseDao.executeQuery(conn, sql.toString(), pstm, rs, params);
            //遍历结果集
            while (rs.next()) {
                providerCounts = rs.getInt("providerCount");
            }
            System.out.println("ProviderDaoImpl-> ProviderCounts：" + providerCounts);

            //最终关闭资源连接
            BaseDao.closeResource(null, pstm, rs);

        }
        return providerCounts;
    }

    //添加供应商的方法
    @Override
    public boolean addProvider(Connection conn, Provider provider) throws SQLException {
        PreparedStatement pstm = null;
        boolean flag = false;
        if (conn != null) {
            //如果连接数据库成功 编写sql语句
            String sql = "insert into smbms_provider (proCode,proName,proContact,proPhone,proAddress,proFax,proDesc,createdBy,creationDate)values(?,?,?,?,?,?,?,?,?)";
            Object[] params = {provider.getProCode(), provider.getProName(), provider.getProContact(), provider.getProPhone(), provider.getProAddress(), provider.getProFax(), provider.getProDesc(), provider.getCreatedBy(), provider.getCreationDate()};

            //执行sql语句
            if (BaseDao.execute(conn, sql, pstm, params) > 0) {
                flag = true;
            }
            //释放资源
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }

    @Override
    //删除供应商的方法
    public boolean deleteProvider(Connection conn, int providerId) throws SQLException {
        PreparedStatement pstm = null;
        boolean flag = false;
        //如果连接不为空
        if (conn != null) {
            //写sql
            String sql = "delete from smbms_provider where id = ?";
            //参数列表
            Object[] params = {providerId};
            //执行sql
            int updateRows = BaseDao.execute(conn, sql, pstm, params);
            if (updateRows > 0) {
                flag = true;
            }
            //释放资源
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }

    @Override
    //根据供应商id查询供应商信息的方法
    public Provider findById(Connection conn, int providerId) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Provider provider = null;
        //如果连接不为空
        if(conn != null){
            //编写sql语句
            String sql = "select * from smbms_provider where id = ?";
            //参数列表
            Object[] params = {providerId};
            //执行sql
            rs = BaseDao.executeQuery(conn, sql, pstm, rs, params);
            //遍历结果集 封装
            if(rs.next()){
                provider = new Provider();
                provider.setProCode(rs.getString("proCode"));;
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
                provider.setCreatedBy(rs.getInt("createdBy"));
                provider.setCreationDate(rs.getTimestamp("creationDate"));
                provider.setModifyDate(rs.getTimestamp("modifyDate"));
                provider.setModifyBy(rs.getInt("modifyBy"));
            }
            //释放资源
            BaseDao.closeResource(null,pstm,rs);
        }
        return provider;
    }

    @Override
    //修改保存供应商信息方法
    public boolean modifyProvider(Connection conn, int id,Provider provider) throws SQLException {
        PreparedStatement pstm = null;
        boolean flag = false;
        if(conn != null){
            //编写sql语句
            String sql = "update smbms_provider set proCode = ?,proName = ?,proDesc = ?,proContact = ?,proPhone = ?,proAddress = ?,proFax = ?,modifyDate = ?,modifyBy = ? where id = ?";
            Object[] params = {provider.getProCode(),provider.getProName(),provider.getProDesc(),provider.getProContact(),
                    provider.getProPhone(),provider.getProAddress(),provider.getProFax(),provider.getModifyDate(),provider.getModifyBy(),id};

            int updateRows = BaseDao.execute(conn, sql, pstm, params);
            if(updateRows > 0){
                flag = true;
            }
            BaseDao.closeResource(null,pstm,null);
        }
        return flag;
    }

}
