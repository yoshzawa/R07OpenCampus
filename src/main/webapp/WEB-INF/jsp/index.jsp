<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ホテルステラ仙台駅前 | トップページ</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            margin: 0;
            padding: 20px;
            background-color: #e0f7fa; /* 明るい水色 */
            display: flex;
            flex-direction: column;
            align-items: center;
            min-height: 100vh;
        }
        .container {
            max-width: 800px;
            width: 100%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            text-align: center;
        }
        h1 {
            color: #00796b; /* 深い緑 */
            border-bottom: 3px solid #00796b;
            padding-bottom: 15px;
            margin-bottom: 30px;
            font-size: 2.2em;
        }
        h2 {
            color: #004d40; /* さらに深い緑 */
            margin-top: 40px;
            margin-bottom: 20px;
            font-size: 1.8em;
        }
        p {
            font-size: 1.1em;
            margin-bottom: 25px;
        }
        .chat-form {
            background-color: #f0fdfa; /* フォーム背景 */
            padding: 25px;
            border-radius: 10px;
            border: 1px solid #b2dfdb; /* 枠線 */
            margin-top: 30px;
        }
        .chat-form label {
            display: block;
            margin-bottom: 15px;
            font-size: 1.1em;
            color: #004d40;
        }
        .chat-form input[type="text"] {
            width: calc(100% - 22px);
            padding: 12px;
            margin-top: 5px;
            border: 1px solid #80cbc4;
            border-radius: 8px;
            font-size: 1em;
            box-sizing: border-box;
        }
        .chat-form input[type="submit"] {
            background-color: #00796b; /* ボタン背景 */
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1.1em;
            transition: background-color 0.3s ease;
            margin-top: 20px;
        }
        .chat-form input[type="submit"]:hover {
            background-color: #004d40; /* ホバー時の色 */
        }
        .info-message {
            margin-top: 20px;
            padding: 15px;
            background-color: #ffe0b2; /* 警告色 */
            border: 1px solid #ffb74d;
            border-radius: 8px;
            color: #e65100;
            font-weight: bold;
        }
        .footer {
            margin-top: 40px;
            font-size: 0.9em;
            color: #555;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>ホテルステラ仙台駅前へようこそ</h1>
        <p>お客様の快適なご滞在をサポートいたします。</p>

        <div class="chat-form">
            <h2>AIコンシェルジュにご質問ください</h2>
            <p>ご不明な点や知りたいことがございましたら、お気軽にご質問ください。</p>
            <form action="chat" method="post">
                <label for="prompt">質問内容:</label>
                <input type="text" id="prompt" name="prompt" size="60" placeholder="例: Wi-Fiのパスワードは？、近くのコンビニはどこですか？">
                <input type="submit" value="質問を送信">
            </form>
            <%-- サーブレットからのメッセージがあれば表示 --%>
            <% if (request.getAttribute("message") != null) { %>
                <div class="info-message">
                    <%= request.getAttribute("message") %>
                </div>
            <% } %>
        </div>

        <div class="footer">
            <p>&copy; 2025 ホテルステラ仙台駅前. All Rights Reserved.</p>
        </div>
    </div>
</body>
</html>
