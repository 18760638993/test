package com.hwx.filter;

import com.hwx.pojo.User;
import com.hwx.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录注销后过滤主页
 */
public class SysFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        //从Session中得到会话
        User user=(User)req.getSession().getAttribute(Constants.USER_SESSION);
        //已经被移除或者注销了，或者未登录
        if (user==null){
            resp.sendRedirect("/smbms/error.jsp");//

        }else {
            chain.doFilter(request,response);
        }
    }

    @Override
    public void destroy() {
    }
}
