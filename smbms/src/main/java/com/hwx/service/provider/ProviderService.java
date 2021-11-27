package com.hwx.service.provider;

import com.hwx.pojo.Provider;

import java.sql.SQLException;
import java.util.List;

/**
 * 供应商Service接口
 */
public interface ProviderService {
    //根据供应商编码 或 供应商名称 查询供应商总数
    int getProviderCounts(String proCode,String proName);

    //查询供应商数据列表
    List<Provider> getProviderList(String proCode, String proName, int currentPageNo, int pageSize);

    //添加供应商的方法
    boolean addProvider(Provider provider)throws SQLException;

    //删除供应商的方法
    boolean deleteProvider(int providerId)throws SQLException;

    //根据供应商id查询供应商信息的方法
    Provider findById(int providerId)throws SQLException;

    //修改供应商信息方法
    boolean modifyProvider(int id,Provider provider)throws SQLException;
}
