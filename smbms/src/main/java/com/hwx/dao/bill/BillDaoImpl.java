package com.hwx.dao.bill;

import com.hwx.dao.BaseDao;
import com.hwx.pojo.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *订单实现类
 */
public class BillDaoImpl implements BillDao{
    @Override
    //根据 商品名称、供应商id、是否付款 查询订单列表
    public List<Bill> getBillInfo(Connection conn, String productName, int providerId, int isPayment, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement pstm  = null;
        ResultSet rs = null;
        List<Bill> bills = new ArrayList<>();
        if(conn != null){
            System.out.println("enter BillDaoImpl...");
            //动态拼接字符串
            StringBuffer sql = new StringBuffer();
            //参数列表
            ArrayList<Object> paramsList = new ArrayList<>();
            sql.append("select b.*,p.proName as proName from smbms_bill b,smbms_provider p where b.providerId = p.id");
            if(productName != "" && providerId != 0 && isPayment != 0){
                //说明三个参数都不为空
                sql.append(" and productName like ? and providerId = ? and isPayment = ?");
                paramsList.add("%"+productName+"%");
                try {
                    paramsList.add(providerId);
                    paramsList.add(isPayment);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(productName != "" || providerId != 0 || isPayment != 0){
                //说明三个参数有些不为空
                if(productName != ""){
                    sql.append(" and productName like ?");
                    paramsList.add("%"+productName+"%");
                }
                if(providerId != 0){
                    sql.append(" and providerId = ?");
                    paramsList.add(providerId);
                }
                if(isPayment != 0){
                    sql.append(" and isPayment = ?");
                    paramsList.add(isPayment);
                }

            }
            //在数据库中 分页使用limit startIndex,pageSize 总数
            //当前页 = (当前页-1)*页面大小
            sql.append(" order by b.creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo-1)*pageSize;
            paramsList.add(currentPageNo);
            paramsList.add(pageSize);
            //sql拼接完成 参数列表也正确
            System.out.println("Test SQL --> "+sql.toString());
            //将参数列表进行转换
            Object[] params = paramsList.toArray();
            //执行sql
            rs = BaseDao.executeQuery(conn, sql.toString(), pstm, rs, params);
            //遍历结果集 封装对象 添加到列表
            while (rs.next()){
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setCreatedBy(rs.getInt("createdBy"));
                bill.setCreationDate(rs.getTimestamp("creationDate"));
                bill.setModifyBy(rs.getInt("modifyBy"));
                bill.setModifyDate(rs.getTimestamp("modifyDate"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("proName"));
                bills.add(bill);
            }
            //关闭资源
            BaseDao.closeResource(null,pstm,rs);
        }
        //返回列表
        return bills;
    }

    @Override
    //根据 商品名称、供应商id、是否付款 查询订单总数
    public int getBillCount(Connection conn, String productName, int providerId, int isPayment) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        if(conn != null){
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_bill b,smbms_provider p where b.providerId = p.id");
            List<Object> paramsList = new ArrayList<>();
            if(productName != "") {
                sql.append(" and productName like ?");
                paramsList.add("%"+productName+"%");
            }
            if(providerId != 0){
                sql.append(" and providerId = ?");
                paramsList.add(providerId);
            }
            if(providerId != 0){
                sql.append(" and isPayment = ?");
                paramsList.add(isPayment);
            }
            Object[] params = paramsList.toArray();
            rs = BaseDao.executeQuery(conn, sql.toString(), pstm, rs, params);
            //遍历结果集 从结果集中取出数量count
            if (rs.next()){
                count = rs.getInt("count");
            }
            //关闭资源
            BaseDao.closeResource(null,pstm,rs);
        }
        return count;
    }

    @Override
    //添加订单
    public boolean addBill(Connection conn, Bill bill) throws SQLException {
        PreparedStatement pstm = null;
        boolean flag  = false;
        if (conn != null){
            String sql = "insert into smbms_bill (billCode,productName,productDesc,productUnit,productCount,totalPrice,isPayment,createdBy,creationDate,modifyBy,modifyDate,providerId) values(?,?,?,?,?,?,?,?,?,?,?,?)" ;
            Object[] params = {bill.getBillCode(),bill.getProductName(),bill.getProductDesc(),bill.getProductUnit(),bill.getProductCount(),bill.getTotalPrice(),bill.getIsPayment(),bill.getCreatedBy(),bill.getCreationDate(),bill.getModifyBy(),bill.getModifyDate(),bill.getProviderId()};
            int updateRows = BaseDao.execute(conn, sql, pstm, params);
            if(updateRows > 0){
                flag = true;
            }
            BaseDao.closeResource(null,pstm,null);
        }
        return flag;
    }

    @Override
    //删除订单
    public boolean deleteBill(Connection conn, int billId) throws SQLException {
        PreparedStatement pstm = null;
        boolean flag = false;
        if(conn != null){
            String sql = "delete from smbms_bill where id = ?";
            Object[] params = {billId};
            int updateRows = BaseDao.execute(conn, sql, pstm, params);
            if(updateRows > 0){
                flag = true;
            }
            BaseDao.closeResource(null,pstm,null);
        }
        return flag;
    }

    @Override
    //根据订单id 获取订单信息
    public Bill findByBillId(Connection conn, int billId) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Bill bill = new Bill();
        if(conn != null){
            String sql = "select b.*,p.proName as providerName from smbms_bill b,smbms_provider p where b.id = ? and b.providerId = p.id";
            Object[] params = {billId};
            rs = BaseDao.executeQuery(conn, sql, pstm, rs, params);
            //遍历此结果集 并存入bill对象
            if(rs.next()){
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setCreatedBy(rs.getInt("createdBy"));
                bill.setCreationDate(rs.getTimestamp("creationDate"));
                bill.setModifyBy(rs.getInt("modifyBy"));
                bill.setModifyDate(rs.getTimestamp("modifyDate"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("providerName"));
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return bill;
    }
    //修改订单信息
    @Override
    public boolean modifyBill(Connection conn, int billId, Bill bill) throws SQLException {
        PreparedStatement pstm = null;
        boolean flag = false;
        if(conn != null){
            String sql = "update smbms_bill set billCode = ?,productName =?,productDesc = ?,productUnit = ?,productCount = ? ,totalPrice = ?,isPayment = ?,modifyBy = ?,modifyDate = ?,providerId = ? where id = ?";
            Object[] params = {bill.getBillCode(),bill.getProductName(),bill.getProductDesc(),bill.getProductUnit(),bill.getProductCount(),bill.getTotalPrice(),bill.getIsPayment(),bill.getModifyBy(),bill.getModifyDate(),bill.getProviderId(),billId};
            int updateRows = BaseDao.execute(conn, sql, pstm, params);
            if (updateRows > 0) {
                flag = true;
            }
            BaseDao.closeResource(null,pstm,null);
        }
        return flag;
    }
}
