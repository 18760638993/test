package com.hwx.service.provider;

import com.hwx.dao.BaseDao;
import com.hwx.dao.provider.ProviderDao;
import com.hwx.dao.provider.ProviderDaoImpl;
import com.hwx.pojo.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 供应商Service接口的实现类
 */
public class ProviderServiceImpl implements ProviderService{
    //Service层调用dao层
    private ProviderDao providerDao;
    public ProviderServiceImpl(){
        providerDao = new ProviderDaoImpl();
    }
    @Override
    //根据供应商编码 或 供应商名称 查询供应商总数
    public int getProviderCounts(String proCode, String proName) {
        Connection conn = null;
        int providerCounts = 0;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            providerCounts = providerDao.getProviderCounts(conn, proCode, proName);
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
            conn.rollback();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return providerCounts;
        }
    }

    @Override
    //查询供应商数据列表
    public List<Provider> getProviderList(String proCode, String proName, int currentPageNo, int pageSize) {
        List<Provider> providerList = null;
        Connection conn = null;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            providerList = providerDao.getProviderInfo(conn, proCode, proName, currentPageNo, pageSize);
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
            conn.rollback();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return providerList;
        }
    }

    @Override
    //添加供应商的方法
    public boolean addProvider(Provider provider) {
        Connection conn = null;
        boolean flag = false;
        try {
            //获取数据库连接
            conn = BaseDao.getConnection();
            //开启事务
            conn.setAutoCommit(false);
            //执行方法
            flag = providerDao.addProvider(conn, provider);
            //提交事务
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
            //事务回滚
            conn.rollback();
        }finally {
            //释放资源
            BaseDao.closeResource(conn,null,null);
            return flag;
        }
    }

    @Override
    //删除供应商的方法
    public boolean deleteProvider(int providerId) {
        Connection conn = null;
        boolean flag = false;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            flag = providerDao.deleteProvider(conn, providerId);
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
            conn.rollback();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return flag;
        }
    }

    @Override
    //根据供应商id查询供应商信息的方法
    public Provider findById(int providerId)  {
        Connection conn = null;
        Provider provider = null;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            provider = providerDao.findById(conn, providerId);
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
            conn.rollback();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return provider;
        }
    }


    @Override
    //修改供应商信息方法
    public boolean modifyProvider(int id, Provider provider)  {
        Connection conn  =  null;
        boolean flag = false;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            flag = providerDao.modifyProvider(conn, id, provider);
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
            conn.rollback();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return flag;
        }
    }
}
