package com.hwx.dao.provider;

import com.hwx.pojo.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 供应商接口
 */
public interface ProviderDao {
    //查询供应商列表数据
    List<Provider> getProviderInfo(Connection conn, String proCode, String proName, int currentPageNo, int pageSize) throws SQLException;

    //根据供应商编码 或 供应商名称 查询供应商总数
    int getProviderCounts(Connection conn, String proCode, String proName) throws SQLException;

    //添加供应商的方法
    boolean addProvider(Connection conn, Provider provider) throws SQLException;

    //删除供应商的方法
    boolean deleteProvider(Connection conn,int providerId)throws SQLException;

    //根据供应商id查询供应商信息的方法
    Provider findById(Connection conn,int providerId)throws SQLException;

    //修改保存供应商信息方法
    boolean modifyProvider(Connection conn,int id,Provider provider)throws SQLException;
}
