<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/plain;charset=UTF-8" language="java"
	pageEncoding="utf-8"%>
<%
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
    boolean comma = false;
%>
<c:set value="false" var="comma" scope="page" />
<c:if test="${not empty success }">
	success:${success }
	<c:set value="true" var="comma" scope="page" />
</c:if>
<c:if test="${not empty exception }">
	<c:if test="${comma}">,</c:if>
	exception:${exception }
	<c:set value="true" var="comma" scope="page" />
</c:if>
<c:if test="${not empty result }">
<c:if test="${comma}">,</c:if>
${result }
<c:set value="true" var="comma" scope="page" />
</c:if>


