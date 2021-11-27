package com.hwx.service.bill;

import com.hwx.dao.BaseDao;
import com.hwx.dao.bill.BillDaoImpl;
import com.hwx.pojo.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *订单Service的实现类
 */
public class BillServiceImpl implements BillService{
    private BillDaoImpl billDao;
    public BillServiceImpl(){
        this.billDao = new BillDaoImpl();
    }
    @Override
    //根据 商品名称、供应商id、是否付款 查询订单总数
    public int getBillCount(String productName, int providerId, int isPayment){
        Connection conn = null;
        int billCount = 0;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            billCount = billDao.getBillCount(conn, productName, providerId, isPayment);
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
            conn.rollback();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return billCount;
        }
    }

    @Override
    //根据 商品名称、供应商id、是否付款 查询订单列表
    public List<Bill> getBillInfo(String productName, int providerId, int isPayment, int currentPageNo, int pageSize) {
        Connection conn = null;
        List<Bill> billList = null;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            billList = billDao.getBillInfo(conn, productName, providerId, isPayment,currentPageNo,pageSize);
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
            conn.rollback();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return billList;
        }
    }

    @Override
    //添加订单
    public boolean addBill(Bill bill) {
        boolean flag = false;
        Connection conn = null;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            flag = billDao.addBill(conn, bill);
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
    //删除订单
    public boolean deleteBill(int billId) {
        boolean flag = false;
        Connection conn = null;
        try {
            conn  = BaseDao.getConnection();
            conn.setAutoCommit(false);
            flag = billDao.deleteBill(conn, billId);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return flag;
        }
    }

    @Override
    //根据订单id 获取订单信息
    public Bill findByBillId(int billId){
        Connection conn = null;
        Bill bill = null;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            bill = billDao.findByBillId(conn, billId);
        }catch (Exception e){
            e.printStackTrace();
            conn.rollback();
        }finally {
            BaseDao.closeResource(conn,null,null);
            return bill;
        }
    }

    @Override
    //修改订单信息
    public boolean modifyBill(int billId, Bill bill) {
        boolean flag = false;
        Connection conn = null;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false);
            flag = billDao.modifyBill(conn, billId, bill);
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
