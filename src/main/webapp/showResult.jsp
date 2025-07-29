<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ChatGPT Response</title>
</head>
<body>
<h2>Response from ChatGPT</h2>
<pre>
<%= request.getAttribute("apiResponse") %>
</pre>
<a href="../input.jsp">Back to Form</a>
</body>
</html>