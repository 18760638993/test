package com.hwx.service.bill;

import com.hwx.pojo.Bill;

import java.sql.SQLException;
import java.util.List;

/**
 * 订单Service
 */
public interface BillService {
    //根据 商品名称、供应商id、是否付款 查询订单总数
    int getBillCount(String productName, int providerId, int isPayment);
    //根据 商品名称、供应商id、是否付款 查询订单列表
    List<Bill> getBillInfo(String productName, int providerId, int isPayment, int currentPageNo, int pageSize);
    //添加订单
    boolean addBill(Bill bill);
    //删除订单
    boolean deleteBill(int billId);
    //根据订单id 获取订单信息
    Bill findByBillId(int billId);
    //修改订单信息
    boolean modifyBill(int billId, Bill bill);
}
