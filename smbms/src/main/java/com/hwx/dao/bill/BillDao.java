package com.hwx.dao.bill;

import com.hwx.pojo.Bill;
import com.hwx.pojo.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 订单
 */
public interface BillDao {
    //根据 商品名称、供应商id、是否付款 查询订单列表
    List<Bill> getBillInfo(Connection conn,  String productName,int providerId,int isPayment, int currentPageNo, int pageSize) throws SQLException;

    //根据 商品名称、供应商id、是否付款 查询订单总数
    int getBillCount(Connection conn, String productName,int providerId,int isPayment) throws SQLException;

    //添加订单
    boolean addBill(Connection conn, Bill bill)throws SQLException;

    //删除订单
    boolean deleteBill(Connection conn, int billId)throws SQLException;

    //根据订单id 获取订单信息
    Bill findByBillId(Connection conn, int billId)throws SQLException;

    //修改订单信息
    boolean modifyBill(Connection conn, int billId, Bill bill)throws SQLException;

}
