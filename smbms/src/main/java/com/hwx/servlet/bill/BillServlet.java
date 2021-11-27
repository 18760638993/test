package com.hwx.servlet.bill;
/**
 * 订单Servlet
 */

import com.alibaba.fastjson.JSONArray;
import com.hwx.pojo.Bill;
import com.hwx.pojo.Provider;
import com.hwx.pojo.User;
import com.hwx.service.bill.BillServiceImpl;
import com.hwx.service.provider.ProviderServiceImpl;
import com.hwx.util.Constants;
import com.hwx.util.PageSupport;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@WebServlet(name = "BillServlet", value = "/BillServlet")
public class BillServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        if(method.equals("query")){
            this.query(request,response);
        }else if(method.equals("getproviderlist")){
            this.getproviderlist(request,response);
        }else if(method.equals("add")){
            this.add(request,response);
        }else if(method.equals("delbill")){
            this.deleteBill(request,response);
        }else if(method.equals("modify")){
            this.findById(request,response);
        }else if(method.equals("modifysave")){
            this.modifysave(request,response);
        }else if(method.equals("view")){
            this.view(request,response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    //查询订单管理列表
    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取搜素信息
        String productName = req.getParameter("queryProductName");
        String providerId = req.getParameter("queryProviderId");
        String isPaymentTemp = req.getParameter("queryIsPayment");

        int proId = 0;
        int isPayment = 0;
        //此属性在搜素按钮那
        String pageIndex = req.getParameter("pageIndex");
        //设置当前页 以及 每页显示的数目
        int currentPageNo = 1;
        int pageSize = 5;

        //对前端传来的属性进行判断 如果为空(没输入) 则将值赋空字符串 dao层实现类判断的时候 也得根据空字符串判断
        if (productName == null){
            productName = "";
        }
        if(providerId != null ){
            proId = Integer.parseInt(providerId);
        }
        if(isPaymentTemp != null ){
            isPayment = Integer.parseInt(isPaymentTemp);
        }
        if(pageIndex!=null){
            currentPageNo = Integer.parseInt(pageIndex);
        }
        //Servlet层调用service层
        BillServiceImpl billService = new BillServiceImpl();
        //获取订单总数 分页：上一页 下一页
        int totalCount = billService.getBillCount(productName,proId,isPayment);

        //总页数支持
        PageSupport pageSupport = new PageSupport();
        System.out.println("当前页："+currentPageNo);
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        System.out.println("获取订单总数"+totalCount);
        pageSupport.setTotalCount(totalCount);
        //总共的页数
        int totalPageCount = pageSupport.getTotalPageCount();
        //控制首页和尾页
        //如果页数小于1，就显示第一页  页数大于 最后一页就 显示最后一页
        if(currentPageNo<1){
            currentPageNo =1;
        }else if(currentPageNo>totalPageCount){
            currentPageNo = totalPageCount;
        }

        //根据前端搜索框信息 查询订单列表
        List<Bill> billList = billService.getBillInfo(productName, proId, isPayment,currentPageNo,pageSize);
        System.out.println("Test billList -> "+billList);
        //将此列表存入req中 供前端展示
        req.setAttribute("billList",billList);
        //查询供应商列表
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        int totalNum = providerService.getProviderCounts("","");
        List<Provider> providerList = providerService.getProviderList("", "", 1, totalNum);
        //将此供应商列表存入req中 为了使我们搜索框搜素内容不清空
        req.setAttribute("providerList",providerList);
        req.setAttribute("queryProductName",productName);
        req.setAttribute("queryProviderId",proId);
        req.setAttribute("queryIsPayment",isPayment);
        //分页显示数据
        req.setAttribute("totalCount",totalCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        //返回前端页面查看
        req.getRequestDispatcher("billlist.jsp").forward(req,resp);
    }
    //查询供应商 billadd.jsp页面中的下拉框调用
    public void getproviderlist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        int providerCounts = providerService.getProviderCounts("", "");
        List<Provider> providerList = providerService.getProviderList("", "", 1, providerCounts);
        //将信息 发送给ajax 将此集合转换为json格式传递
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        //JSONArray 阿里巴巴的JSON工具类 用途就是：转换格式
        out.write(JSONArray.toJSONString(providerList));
        out.flush();
        out.close();
    }

    //添加订单
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取数据 并封装
        Bill bill = new Bill();
        bill.setBillCode(req.getParameter("billCode"));
        bill.setProductName(req.getParameter("productName"));
        bill.setProductUnit(req.getParameter("productUnit"));
        bill.setProductCount(BigDecimal.valueOf(Long.parseLong(req.getParameter("productCount"))));
        bill.setTotalPrice(BigDecimal.valueOf(Long.parseLong(req.getParameter("totalPrice"))));
        bill.setProviderId(Integer.parseInt(req.getParameter("providerId")));
        bill.setIsPayment(Integer.parseInt(req.getParameter("isPayment")));
        //下面是表单没有的
        bill.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setCreationDate(new Date());
        bill.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setModifyDate(new Date());

        BillServiceImpl billService = new BillServiceImpl();
        if(billService.addBill(bill)){
            //如果添加成功 重定向跳转到展示订单页面
            resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
        }else{
            //添加失败 转发到此添加页面
            req.getRequestDispatcher("billadd.jsp").forward(req,resp);
        }
    }

    //删除订单
    public void deleteBill(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //从前端获取要删除的订单id
        String billid = req.getParameter("billid");
        int id = 0;
        //转换
        try {
            id = Integer.parseInt(billid);
        }catch (Exception e){
            e.printStackTrace();
            id = 0;
        }
        BillServiceImpl billService = new BillServiceImpl();
        //创建一个map集合 存储 删除成功和失败的信息 传递给ajax
        Map<Object, Object> resultMap = new HashMap<>();
        if(id <= 0){
            resultMap.put("delResult","notexist");
        }else{
            if(billService.deleteBill(id)){
                //删除订单成功
                resultMap.put("delResult","true");
            }else{
                resultMap.put("delResult","false");
            }
        }
        //将此map集合 转换为json格式
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        //JSONArray 阿里巴巴的JSON工具类 用途就是：转换格式
        out.write(JSONArray.toJSONString(resultMap));
        out.flush();
        out.close();
    }

    // 要修改的订单信息 展示
    public void findById(HttpServletRequest req, HttpServletResponse resp) throws  ServletException, IOException {
        //从前端获取要修改的订单id
        String billid = req.getParameter("billid");
        int id = 0;
        try {
            id = Integer.parseInt(billid);
        }catch (Exception e){
            e.printStackTrace();
            id = 0;
        }
        //根据此id查询订单的信息并在修改页面展示
        BillServiceImpl billService = new BillServiceImpl();
        Bill bill = billService.findByBillId(id);
        bill.setId(id);
        //将此bill存入req 并转发到 billmodify.jsp页面
        req.setAttribute("bill", bill);
        req.getRequestDispatcher("billmodify.jsp").forward(req, resp);

    }
    //修改订单信息方法
    public void modifysave(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //从前端获取要修改的订单id
        String billid = req.getParameter("billid");
        int id = 0;
        try {
            id = Integer.parseInt(billid);
        }catch (Exception e){
            e.printStackTrace();
            id = 0;
        }
        //从前端获取供应商信息
        String billCode = req.getParameter("billCode");
        String productName = req.getParameter("productName");
        String productUnit = req.getParameter("productUnit");
        String productCount = req.getParameter("productCount");
        String totalPrice = req.getParameter("totalPrice");
        String providerId = req.getParameter("providerId");
        String isPayment = req.getParameter("isPayment");
        //根据id查询旧订单信息
        BillServiceImpl billService = new BillServiceImpl();
        //注意：不能直接new一个Bill对象，因为并没有全部修改所有字段，如创建时间是最开始才有的，必须保留
//        Bill bill = billService.findByBillId(id);
        Bill bill = new Bill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductUnit(productUnit);
        bill.setProductCount(BigDecimal.valueOf(Double.parseDouble(productCount)));
        bill.setTotalPrice(BigDecimal.valueOf(Double.parseDouble(totalPrice)));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setIsPayment(Integer.parseInt(isPayment));
        //下面是表单未显示的
        bill.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setModifyDate(new Date());
        if(id>0){

            //执行修改语句
            if(billService.modifyBill(id, bill)){
                //如果修改成功 重定向到订单展示页面
                resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
            }else{
                //修改失败 转发到此修改页面
                req.getRequestDispatcher("billmodify.jsp").forward(req,resp);
            }
        }
    }

    //查看 订单信息
    public void view(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取要查看订单信息的id
        String BillId = req.getParameter("billid");
        int id = 0;
        try {
            id = Integer.parseInt(BillId);
        }catch (ClassCastException e){
            e.printStackTrace();
            id = 0;
        }
        if(id >0) {
            //根据id查询订单信息
            BillServiceImpl billService = new BillServiceImpl();
            Bill bill = billService.findByBillId(id);
            //将信息存入req
            req.setAttribute("bill", bill);
            //转发到billview.jsp页面
            req.getRequestDispatcher("billview.jsp").forward(req, resp);
        }
    }
}
