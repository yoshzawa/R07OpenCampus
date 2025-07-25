<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ChatGPTクエリフォーム</title>
</head>
<body>
    <h2>動作確認</h2>
    <form action="chat" method="post">
        <label>質問: 
            <input type="text" name="prompt" size="60">
        </label>
        <input type="submit" value="送信">
    </form>
</body>
</html>