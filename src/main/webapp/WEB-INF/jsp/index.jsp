<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%
String message=(String)request.getAttribute("message");
%>
<%@ include file="../jspf/header.jspf" %>
<title>ホテルステラ仙台駅前 | トップページ</title>
    <div class="container">
        <h1>ホテルステラ仙台駅前へようこそ</h1>
        <p>お客様の快適なご滞在をサポートいたします。</p>

        <div class="chat-form">
            <h2>AIコンシェルジュにご質問ください</h2>
            <p>ご不明な点や知りたいことがございましたら、お気軽にご質問ください。</p>
            <form action="./index.html" method="post">
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
    </div>
    <%@ include file="../jspf/footer.jspf" %>
</body>
</html>
