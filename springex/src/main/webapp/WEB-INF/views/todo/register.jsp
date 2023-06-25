<%--
  Created by IntelliJ IDEA.
  User: demian
  Date: 2023/06/26
  Time: 8:19 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/todo/register" method="post">
    <div>
        Title : <input type="text" name="title">
    </div>
    <div>
        DueDate: <input type="date" name="dueDate" value="2023-06-25">
    </div>
    <div>
        Writer: <input type="text" name="writer">
    </div>
    <div>
        Finished: <input type="checkbox" name="finished">
    </div>
    <div>
        <button type="submit">Register</button>
    </div>
</form>

</body>
</html>
