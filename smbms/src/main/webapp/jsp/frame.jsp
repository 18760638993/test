<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ page import="com.hwx.util.Constants" %>
<%@ page import="com.hwx.pojo.User" %>
<%@include file="/jsp/common/head.jsp"%>

<div class="right">
    <img class="wColck" src="${pageContext.request.contextPath}/images/clock.jpg" alt=""/>
    <div class="wFont">
        <h2>${ userSession.userName }</h2>
        <p>欢迎来到大熊超市订单管理系统!</p>
    </div>
</div>
</section>
<%@include file="/jsp/common/foot.jsp" %>
