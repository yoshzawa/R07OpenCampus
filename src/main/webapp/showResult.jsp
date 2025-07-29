<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ChatGPT Response</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="container">
        <h2>Response from ChatGPT</h2>
        <pre>
<%= request.getAttribute("apiResponse") %>
        </pre>
        <a href="../input.jsp">Back to Form</a>
    </div>
</body>
</html>