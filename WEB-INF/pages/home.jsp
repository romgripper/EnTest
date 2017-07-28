<%@ page language="java" contentType="text/html; charset=utf-8"
  pageEncoding="utf-8"%>
  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>Welcome ${user.username}</title>
</head>
<body>
  <h2><a href="${pageContext.request.contextPath}/logout">Logout</a></h2>
  <h1>${user.username}'s notebooks:</h1>
  <c:forEach var="notebook" items="${notebooks}" >
    <h1><a href="${pageContext.request.contextPath}/viewNotebook?notebook=${notebook.guid}">${notebook.name}</a></h1>
  </c:forEach>
</body>
</html>