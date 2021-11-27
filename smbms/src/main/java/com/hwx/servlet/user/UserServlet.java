package com.hwx.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.hwx.pojo.Role;
import com.hwx.pojo.User;
import com.hwx.service.role.RoleServiceImpl;
import com.hwx.service.user.UserService;
import com.hwx.service.user.UserServiceImpl;
import com.hwx.util.Constants;
import com.hwx.util.PageSupport;
import com.mysql.jdbc.StringUtils;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户Servlet
 */
public class UserServlet extends HttpServlet {
    //控制层 调用业务层代码 进行判断
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        if (method.equals("savePwd")){
            this.updatePwd(request,response);
        }else if(method.equals("pwdmodify")){
            this.verifyPwd(request,response);
        }else if(method.equals("query")){
            this.query(request,response);
        }else if(method.equals("add")){
            this.add(request,response);
        }else if(method.equals("deluser")){
            this.deleteUser(request,response);
        }else if(method.equals("modify")){
            this.findById(request,response);
        }else if(method.equals("modifyexe")){
            this.modifyUserInfo(request,response);
        }else if(method.equals("getRoleList")){
            this.getRoleList(request,response);
        }else if(method.equals("ifExist")) {//ucexist
            this.ifExist(request,response);//userCodeExist
        }else if(method.equals("view")){
            this.viewUser(request,response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    //查询用户列表
    public void query(HttpServletRequest req, HttpServletResponse resp){
        //从前端获取数据
        String queryUserName = req.getParameter("queryName");//用户名
        String userRoleTemp =req.getParameter("queryUserRole");//用户角色  //值为0 、1、2、3
        String pageIndex = req.getParameter("pageIndex");//页码 第几页
        int queryUserRole = 0;//默认角色对应的值为0
        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList =null;
        //第一次走这个请求，默认是第一页，页码显示数据行数固定totalPageCount
        int pageSize = 5;// 每5条数据分一页     可以把这个些到配置文件中，方便后期修改;
        int currentPageNo = 1;// 当前页码是第一页

        if (queryUserName == null){
            queryUserName="";
        }
        if (userRoleTemp != null && !userRoleTemp.equals("")){
            queryUserRole = Integer.parseInt(userRoleTemp);// 把前台传递进来的角色对应的值赋值给queryUserRole
        }
        if (pageIndex != null){
            currentPageNo = Integer.parseInt(pageIndex);//把页码 第几页  赋值给当前第几页的页码
        }
        //获取用户的总数量 （分页：上一页 下一页 ）
        int totalCount = userService.getUserCounts(queryUserName,queryUserRole);
        //总页数的工具类
        PageSupport pageSupport =new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);//当前第几页
        pageSupport.setPageSize(pageSize);//每页数量
        pageSupport.setTotalCount(totalCount);//总共几条数据

        int totalPageCount = pageSupport.getTotalPageCount();//总共分几页
        //int totalPageCount1 =((int) (totalCount/pageSize))+1;

        //控制首页和尾页
        //如果当前页面的数字小于1，则默认显示第一页的数据,如果大于最后一页，则默认显示最后一页
        if(currentPageNo<1){
            currentPageNo =1;
        }else if (currentPageNo>totalPageCount){
            currentPageNo=totalPageCount;
        }
        //获取用户列表显示
        userList =userService.getUserList(queryUserName,queryUserRole,currentPageNo,pageSize);
        req.setAttribute("userList",userList);
        //获取角色列表显示
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        //返回数据给前端

        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount",totalCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);
        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //修改密码
    public void updatePwd(HttpServletRequest request, HttpServletResponse response){
        //从获取SessionID
        Object obj = request.getSession().getAttribute(Constants.USER_SESSION);
        //获取前端页面传来的新密码
        String newpassword = request.getParameter("newpassword");
        boolean flag = false;
        if (obj != null && !StringUtils.isNullOrEmpty(newpassword)){
            User user = (User) obj;
            UserService userService = new UserServiceImpl();
            //int id=((User)obj).getId();
            flag = userService.updatePwd(user.getId(),newpassword);
            if (flag){
                request.setAttribute("message","密码修改成功，请重新登录");
                //移除用户的Constants.USER_SESSION，强制让用户重新登录
                request.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                request.setAttribute("message","修改失败，请重新修改");
            }
        }else {
            request.setAttribute("message","新密码不能为空，请重新修改");
        }
        try {
            request.getRequestDispatcher("pwdmodify.jsp").forward(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //验证密码的方法
    public void verifyPwd(HttpServletRequest req, HttpServletResponse resp)throws  IOException{
        //依旧从session中取ID
        Object obj = req.getSession().getAttribute(Constants.USER_SESSION);
        //取 前端传来的旧密码
        String oldpassword = req.getParameter("oldpassword");
        //将结果存放在map集合中 让Ajax使用
        Map<String, String> resultMap = new HashMap<String, String>();
        //下面开始判断 键都是用result 此处匹配js中的Ajax代码
        if(obj == null){
            //说明session被移除了 或未登录|已注销
            resultMap.put("result","sessionerror");
        }else if(oldpassword == null){
            //前端输入的密码为空
            resultMap.put("result","error");
        }else {
            //如果旧密码与前端传来的密码相同
            if(((User)obj).getUserPassword().equals(oldpassword)){
                resultMap.put("result","true");
            }else{
                //前端输入的密码和真实密码不相同
                resultMap.put("result","false");
            }
        }
        //上面已经封装好 现在需要传给Ajax 格式为json 所以我们得转换格式
        resp.setContentType("application/json");//将应用的类型变成json
        PrintWriter writer = resp.getWriter();
        //JSONArray 阿里巴巴的JSON工具类 用途就是：转换格式
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    //添加用户方法
    public void add(HttpServletRequest req, HttpServletResponse resp)throws IOException,ServletException {
//        System.out.println("进入add方法");
        //从前端获取数据
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");
        //对数据进行封装
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(Integer.valueOf(gender));//parseInt(gender)
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        }catch (ParseException e){
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.parseInt(userRole));
        //注意这两个参数不在表单的填写范围内
        user.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setCreateDate(new Date());
//        System.out.println("封装好的："+user.getUserCode());
        //调用service执行添加方法
        UserServiceImpl userService = new UserServiceImpl();
        boolean flag  = userService.addUser(user);
        if(flag){
            //说明执行成功 网页重定向到 用户管理页面(即 查询全部用户列表)
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else{
            //说明 添加失败 转发到此 添加页面
            req.getRequestDispatcher("useradd.jsp").forward(req,resp);
        }
    }

    //用户管理模块中的删除用户
    public void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //从前端获取 要删除的用户 的信息
        String userid = req.getParameter("uid");//uid
        int delId = 0;
        //先转换
        try {
            delId= Integer.parseInt(userid);
        }catch (Exception e){
            e.printStackTrace();
            delId = 0;
        }
        //将结果存放在map集合中 让Ajax使用
        Map<String, String> resultMap = new HashMap<>();
        if(delId<=0){
            resultMap.put("delResult","notexist");
        }else {
            UserServiceImpl userService = new UserServiceImpl();
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult","true");
            }else {
                resultMap.put("delResult", "false");
            }
        }
        //上面已经封装好 现在需要传给Ajax 格式为json 所以我们得转换格式
        resp.setContentType("application/json");//将应用的类型变成json
        PrintWriter writer = resp.getWriter();
        //JSONArray 阿里巴巴的JSON工具类 用途就是：转换格式
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    //用户管理模块中的功能 —— 根据id查询用户信息
    public void findById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取 要修改的用户 的id
        String id = req.getParameter("uid");
        int userId = 0;
        try {
            userId = Integer.parseInt(id);
        }catch (Exception e){
            e.printStackTrace();
            userId = 0;
        }
        UserServiceImpl userService = new UserServiceImpl();
        //查询要更改的用户信息
        User user = userService.findById(userId);
        //将用户信息保存至 request中 让usermodify.jsp显示
        req.setAttribute("user",user);
        req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
    }

    //用户管理模块中的更改用户信息
    public void modifyUserInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //从前端获取 要修改的用户 的id
        String id = req.getParameter("uid");
        int userId = 0;
        try {
            userId = Integer.parseInt(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        //从修改信息的表单中封装信息
        UserServiceImpl userService = new UserServiceImpl();
        User user = new User();//userService.findById(userId);
        user.setUserName(req.getParameter("userName"));
        user.setGender(Integer.parseInt(req.getParameter("gender")));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("birthday")));
        }catch (ParseException e){
            e.printStackTrace();
        }
        user.setPhone(req.getParameter("phone"));
        user.setAddress(req.getParameter("address"));
        user.setUserRole(Integer.parseInt(req.getParameter("userRole")));
        //注意这两个参数不在表单的填写范围内
        user.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());


        if(userService.modifyUserInfo(userId,user)){
            //如果执行成功了 网页重定向到 用户管理页面(即 查询全部用户列表)
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else{
            //说明 添加失败 转发到此 添加页面
            req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
        }
    }

    //用户管理模块中  (添加用户——表单中的用户角色下拉框)
    public void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Role> roleList = null;
        RoleServiceImpl roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList1 转换为json对象输出
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.write(JSONArray.toJSONString(roleList));
        out.flush();
        out.close();
    }

    //用户管理模块 子模块(验证用户编码是否已经存在)
    public void ifExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //获取前端输入 的用户编码
        String userCode = req.getParameter("userCode");
        //将结果存放在map集合中 让Ajax使用
        Map<String, String> resultMap = new HashMap<>();
        if(userCode == null || userCode.equals("")){
            System.out.println("前端未填写用户编码...");
            resultMap.put("userCode","NoWrite");
        }else{
            System.out.println("前端填写了用户编码...");
            UserServiceImpl userService = new UserServiceImpl();
            User isNullUser = userService.login(userCode, "");

            //判断是否已经存在这个用户编码
            boolean flag = isNullUser != null ? true : false;
            if(flag){
                //用户编码存在
                //将信息存入map中
                resultMap.put("userCode","exist");
            }
        }
        //上面已经封装好 现在需要传给Ajax 格式为json 所以我们得转换格式
        resp.setContentType("application/json");//将应用的类型变成json
        PrintWriter writer = resp.getWriter();
        //JSONArray 阿里巴巴的JSON工具类 用途就是：转换格式
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();

    }


    //用户管理模块中的子模块 —— 查询用户信息
    public void viewUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取 要查询用户 的id
        String id = req.getParameter("uid");
        int userId = 0;
        try {
            userId = Integer.parseInt(id);
        }catch (Exception e){
            e.printStackTrace();
            userId = 0;
        }
        //调用 根据id查询用户信息的方法
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.findById(userId);
        //将此user发送到展示前端 的页面进行展示
        req.setAttribute("user",user);
        //跳转到前端 的展示页面
        req.getRequestDispatcher("userview.jsp").forward(req,resp);
    }














    @Test
    public void test(){

    }

}
