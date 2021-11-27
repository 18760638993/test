package com.hwx.servlet.provider;
/**
 * 供应商Servlet
 */

import com.alibaba.fastjson.JSONArray;
import com.hwx.pojo.Provider;
import com.hwx.pojo.User;
import com.hwx.service.provider.ProviderServiceImpl;
import com.hwx.util.Constants;
import com.hwx.util.PageSupport;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//@WebServlet(name = "ProviderServlet", value = "/ProviderServlet")
public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        if(method.equals("query")){
            this.query(request,response);
        }else if(method.equals("add")){
            this.add(request,response);
        }else if(method.equals("delprovider")){
            this.deleteProvider(request,response);
        }else if(method.equals("modify")){
            this.findById(request,response);
        }else if(method.equals("modifyexe")){
            this.modify(request,response);
        }else if(method.equals("view")){
            this.view(request,response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    //查询 供应商 列表的方法
    public void query(HttpServletRequest req, HttpServletResponse resp) {
        //从providerlist.jsp中获取传来的数据
        String proCode = req.getParameter("queryProCode");
        String proName = req.getParameter("queryProName");
        String pageIndex = req.getParameter("pageIndex");
        List<Provider> providerList = null;
        int currentPageNo = 1;
        int pageSize = 5;
        //还需判断前端传来的数据是否为空
        if (proCode == null) {
            proCode = "";
        }
        if (proName == null ){
            proName = "";
        }
        if(pageIndex!=null){
            currentPageNo = Integer.parseInt(pageIndex);//把页码 第几页  赋值给当前第几页的页码
        }
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        //获取用户的总数量 （分页：上一页 下一页 ）
        int totalCount = providerService.getProviderCounts(proCode, proName);
        //总页数的工具类
        PageSupport pageSupport =new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);//当前第几页
        pageSupport.setPageSize(pageSize);//每页数量
        pageSupport.setTotalCount(totalCount);//总共几条数据

        int totalPageCount = pageSupport.getTotalPageCount();//总共分几页
        //控制首页和尾页
        //如果当前页面的数字小于1，则默认显示第一页的数据,如果大于最后一页，则默认显示最后一页
        if(currentPageNo<1){
            currentPageNo =1;
        }else if (currentPageNo>totalPageCount){
            currentPageNo=totalPageCount;
        }
        //获取符合条件的 信息
        providerList= providerService.getProviderList(proCode, proName, currentPageNo, pageSize);
        //将信息存入requset 使得在前端展示
        req.setAttribute("providerList",providerList);
        req.setAttribute("totalCount",totalCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("queryProCode",proCode);
        req.setAttribute("queryProName",proName);
        //返回前端
        try {
            req.getRequestDispatcher("providerlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //添加 供应商方法
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //从前端 获取供应商的信息
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");
        //下面的参数自己获取
        int createdBy = ((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId();

        //将信息封装成一个供应商对象
        Provider provider = new Provider();
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setProDesc(proDesc);
        provider.setCreatedBy(createdBy);
        provider.setCreationDate(new Date());
        //调用service层方法
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        boolean flag = providerService.addProvider(provider);
        //如果成功 则重定向到providerlist.jsp页面
        if(flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        }else{
            //失败 跳转到添加页面
            req.getRequestDispatcher("provideradd.jsp").forward(req,resp);
        }

    }

    //删除供应商的方法
    public void deleteProvider(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //从前端获取 要删除的供应商 的id
        String proid = req.getParameter("proid");
        int id = 0;
        try {
            id = Integer.parseInt(proid);
        }catch (Exception e){
            e.printStackTrace();
            id = 0;
        }
        //将信息存入一个map集合中 传给ajax
        HashMap<Object, Object> resultMap = new HashMap<>();
        if(id<=0){
            resultMap.put("delResult","notexist");
        }else{
            ProviderServiceImpl providerService = new ProviderServiceImpl();
            if(providerService.deleteProvider(id)){
                //如果删除成功
                resultMap.put("delResult","true");
            }else{
                resultMap.put("delResult","false");
            }
        }
        //将此map集合转换成json格式传递
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        //JSONArray 阿里巴巴的JSON工具类 用途就是：转换格式
        out.write(JSONArray.toJSONString(resultMap));
        out.flush();
        out.close();
    }

    /**
     * 思路 ： 我们点击修改按钮 跳转到修改信息页面 并显示旧信息
     * 所以我们需先定义一个根据id查询用户信息的方法
     * 当点击修改按钮时先调用 根据id查询供应商信息的方法
     * 将信息在页面中展示出来
     * 点击修改信息页面的保存按钮 再对信息进行修改
     * 总结一句话：先查询展示 再修改保存
     */
    //根据id查询供应商信息
    public void findById(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取id
        String proid = req.getParameter("proid");
        int id = 0;
        try {
            id = Integer.parseInt(proid);
        }catch (Exception e){
            e.printStackTrace();
            id = 0;
        }
        if(id>0){
            ProviderServiceImpl providerService = new ProviderServiceImpl();
            Provider provider = providerService.findById(id);
            //设置id 让修改提交时可获取
            provider.setId(id);
            //将供应商信息存至 req
            req.setAttribute("provider",provider);
            //返回至前端展示页面
            req.getRequestDispatcher("providermodify.jsp").forward(req,resp);
        }
    }

    //修改保存供应商信息
    public void modify(HttpServletRequest req,HttpServletResponse resp) throws  IOException, ServletException {
        //从前端获取 要修改的供应商的id信息
        String proId =  req.getParameter("proid");
//        System.out.println("proId : ->"+proId.toString());
        int id = 0;
        try {
            id = Integer.parseInt(proId);
        }catch (Exception e){
            e.printStackTrace();
            id = 0;
        }
        //从前端获取供应商信息
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");
        //封装成一个对象
        ProviderServiceImpl providerService = new ProviderServiceImpl();
//        Provider provider = providerService.findById(id);
        Provider provider =new Provider();
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setProDesc(proDesc);
        //下面的参数不是由前端传来的
        provider.setModifyDate(new Date());
        provider.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        if(id>0){
            //执行更改
            if(providerService.modifyProvider(id, provider)){
                //如果修改成功 重定向到展示供应商列表页面
                resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
            }else{
                //修改失败 转发到此修改页面
                req.getRequestDispatcher("providermodify.jsp").forward(req,resp);
            }
        }
    }

    //查看 供应商信息方法
    public void view(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取供应商的id
        String proid = req.getParameter("proid");
        int id = 0;
        try {
            id = Integer.parseInt(proid);
        }catch (Exception e){
            e.printStackTrace();
            id = 0 ;
        }
        //根据id查询
        if(id >0){
            ProviderServiceImpl providerService = new ProviderServiceImpl();
            Provider provider = providerService.findById(id);
            //将此对象传到providerview.jsp进行展示
            req.setAttribute("provider",provider);
            //重定向到展示页
            req.getRequestDispatcher("providerview.jsp").forward(req,resp);
        }
    }

}
