package com.hwx.servlet.user;

import com.hwx.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出系统如何跳转
 */
public class LogoutServlet extends HttpServlet {
    //控制层 调用业务层代码 进行判断
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //移除用户的Constants.USER_SESSION
        request.getSession().removeAttribute(Constants.USER_SESSION);
        //返回登录页面
        response.sendRedirect(request.getContextPath()+"/login.jsp");



    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
