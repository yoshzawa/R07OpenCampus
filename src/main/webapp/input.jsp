<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AIコンシェルジュ デバッグ設定</title>
    <link rel="stylesheet" type="text/css" href="./css/style.css">

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
            <div class="prompt-controls">
                <label for="presetPrompt">プリセットを選択:</label>
                <select id="presetPrompt">
                    <option value="">カスタム入力</option>
                    <option value="交通関連の情報であれば5を、それ以外は0を返してください。それ以外は返さないようにしてください。数字一文字だけ返してください。それ以外は返さないでください。">交通関連 (5/0)</option>
                    <option value="館内施設に関する情報であれば1を、レストランに関する情報であれば2を、それ以外は0を返してください。数字一文字だけ返してください。それ以外は返さないでください。">館内施設/レストラン (1/2/0)</option>
                    <option value="館内施設に関する情報であれば1を、レストランに関する情報であれば2を、観光や主要スポットに関しては3を、グルメやショッピングに関しては4を、交通関連の情報であれば5を、非常時の避難経路に関しては6を、備品やサービスについては7を、よくある質問については8を、それ以外は0を返してください。数字一文字だけ返してください。それ以外は返さないでください。">館内施設/レストラン (1-8/0)</option>
                </select>
            </div>
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

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const presetSelect = document.getElementById('presetPrompt');
            const systemPromptTextarea = document.getElementById('systemPromptInput');

            presetSelect.addEventListener('change', function() {
                if (this.value !== "") { // "カスタム入力" 以外が選択された場合
                    systemPromptTextarea.value = this.value;
                }
                // "カスタム入力" が選択された場合は何もしない（ユーザーが手動で入力できるようにするため）
            });
        });
    </script>
</body>
</html>
