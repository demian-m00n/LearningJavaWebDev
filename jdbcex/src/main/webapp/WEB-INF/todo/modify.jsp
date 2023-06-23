<%--
  Created by IntelliJ IDEA.
  User: demian
  Date: 2023/06/22
  Time: 6:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Todo Modify</title>
</head>
<body>

<form action="/todo/modify" id="form1" method="post">
  <div>
    <input type="text" name="tno" value="${dto.tno}" readonly>
  </div>
  <div>
    <input type="text" name="title" value="${dto.title}" >
  </div>
  <div>
    <input type="date" name="dueDate" value="${dto.dueDate}" >
  </div>
  <div>
    <input type="checkbox" name="finished" ${dto.finished ? "checked" : ""} >
  </div>
  <div>
    <button type="submit">Modify</button>
  </div>
</form>

<form action="/todo/remove" name="form2" method="post">
  <input type="hidden" name="tno" value="${dto.tno}" readonly>
  <div>
    <button type="submit">Remove</button>
  </div>
</form>

</body>
</html>
