package com.hwx.servlet.user;
/**
 *用户登录控制层
 */

import com.hwx.pojo.User;
import com.hwx.service.user.UserService;
import com.hwx.service.user.UserServiceImpl;
import com.hwx.util.Constants;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

//@WebServlet(name = "LoginServlet", value = "/LoginServlet")
//处理登录请求的servlet
public class LoginServlet extends HttpServlet {
    //控制层 调用业务层代码 进行判断
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取登录页面传来的信息
        String userCode = request.getParameter("userCode");
        String userPassword = request.getParameter("userPassword");
        //和数据库中的密码进行对比，调用业务层
        UserService userService = new UserServiceImpl();
        //把登录的人的信息查到
        User user = userService.login(userCode, userPassword);
        //判断
        if (user != null ) {
            //查有此人
            //判断密码是否正确
            if (user.getUserPassword().equals(userPassword)){
                //将用户的信息放在session中
                request.getSession().setAttribute(Constants.USER_SESSION,user);
                //跳转到主页
                response.sendRedirect("jsp/frame.jsp");
            }else {
                //查无此人
                //转发回登录页面 提示 用户名或密码错误
                request.setAttribute("error","密码错误，请重新输入！");
                request.getRequestDispatcher("login.jsp").forward(request,response);
            }

        }else{
            //查无此人
            //转发回登录页面 提示 用户名或密码错误
            request.setAttribute("error","用户名错误，请重新输入！");
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
