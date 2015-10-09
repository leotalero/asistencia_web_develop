<%@ page language="java" contentType="text/html; charset=iso-8859-1" pageEncoding="iso-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:useBean id="consHermes" class="co.sistemcobro.hermes.constante.Constante" scope="session"/>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<fmt:setLocale value="es_US" scope="application"/>
<jsp:useBean id="now" class="java.util.Date"/>
