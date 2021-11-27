package com.hwx.filter; /**
 * 过滤字符编码
 */

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

//@WebServlet(name = "CharacterEncodigServlet", value = "/CharacterEncodigServlet")
public class CharacterEncodigFilter implements Filter{


    //初始化:web服务器启动，就以及初始化了，随时等待过滤对象出现!|
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

//        System.out.println("CharacterEncodigFilter初始化");
    }

    /*
    过滤：
     //chain链
    1过滤中的所有代码，在过滤特定请求的时候都会执行
    2.必须要让过滤器继续同行
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //解决中文乱码
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        /*
        字符集过滤器上不设置成respone.setContentType(text/html; charset=utf-8)呢？
        因为这样的话你设置的css，js都会变成html
         */
//        response.setContentType("text/html;charset=utf-8");
//        System.out.println("CharacterEncodigFilter执行前-----");
        chain.doFilter(request,response);//让我们的请求继续走，如果不写，程序到这里就被拦截停止!
//        System.out.println("CharacterEncodigFilter执行后-----");


    }

    //销毁: web服务器关闭的时候,过滤会销毁
    @Override
    public void destroy() {
//        System.out.println("CharacterEncodigFilter销毁");
//        Filter.super.destroy();
    }
}
