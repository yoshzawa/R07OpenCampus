<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AIコンシェルジュ デバッグ設定</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f0f8ff; }
        .container { max-width: 800px; margin: auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1, h2 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; margin-top: 30px; }
        p { margin-bottom: 15px; }
        form { margin-top: 20px; padding: 20px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9; }
        label { display: block; margin-bottom: 8px; font-weight: bold; }
        textarea { width: calc(100% - 20px); padding: 10px; border: 1px solid #ddd; border-radius: 4px; resize: vertical; min-height: 100px; box-sizing: border-box; }
        input[type="submit"] { background-color: #3498db; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; margin-top: 15px; }
        input[type="submit"]:hover { background-color: #2980b9; }
        .info-message { margin-top: 20px; padding: 10px; background-color: #d4edda; border: 1px solid #28a745; border-radius: 5px; color: #155724; }
        .error-message { margin-top: 20px; padding: 10px; background-color: #f8d7da; border: 1px solid #dc3545; border-radius: 5px; color: #721c24; }
        .back-link { display: block; text-align: center; margin-top: 30px; }
        .back-link a { color: #3498db; text-decoration: none; font-weight: bold; }
        .back-link a:hover { text-decoration: underline; }
    </style>
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
