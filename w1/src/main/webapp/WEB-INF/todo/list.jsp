<%--
  Created by IntelliJ IDEA.
  User: demian
  Date: 2023/06/17
  Time: 2:02 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>List Page</h1>

<ul>
    <c:forEach var="dto" items="{$list}">
        <li>${dto}</li>
    </c:forEach>
</ul>
</body>
</html>
