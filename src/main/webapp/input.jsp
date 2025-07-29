<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AIコンシェルジュ デバッグ設定</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>AIコンシェルジュ デバッグ設定</h1>

        <%-- メッセージ表示エリア --%>
        <% if (request.getAttribute("message") != null) { %>
            <div class="info-message">
                <%= request.getAttribute("message") %>
            </div>
        <% } %>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <h2>現在のシステムプロンプト設定</h2>
        <form action="./debug" method="post">
            <label for="systemPromptInput">新しいシステムプロンプト:</label>
            <textarea id="systemPromptInput" name="systemPrompt" rows="10"><%= request.getAttribute("currentSystemPrompt") != null ? request.getAttribute("currentSystemPrompt") : "" %></textarea>
            <input type="submit" value="システムプロンプトを設定">
        </form>

        <h2>動作確認</h2>
        <p>設定したプロンプトがAIにどのように影響するか、ここで質問を送信して確認できます。</p>
        <form action="./setting/result" method="post"> <%-- 既存のフォームはそのまま --%>
            <label for="prompt">質問:</label>
            <input type="text" id="prompt" name="prompt" size="60">
            <input type="submit" value="送信">
        </form>
        
        <div class="back-link">
            <a href="./">トップページに戻る</a>
        </div>
    </div>
</body>
</html>
