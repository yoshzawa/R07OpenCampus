<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/jsp_style.css">
    <style>
        /* hotel_info.jsp固有のスタイル */
        p.note { font-size: 0.9em; color: #7f8c8d; }
    </style>
    <title>ホテル基本情報と館内施設のご案内 | ホテルステラ仙台駅前</title>
</head>
<body>
<%@ include file="../jspf/header.jspf" %>
    <div class="container">
        <h1>ホテル基本情報と館内施設のご案内</h1>
        <p>ホテルステラ仙台駅前をご利用いただき、誠にありがとうございます。当ホテルの基本情報と館内施設をご案内いたします。</p>

        <h2>ホテル基本情報</h2>
        <ul>
            <li><strong>フロント営業時間:</strong> 24時間対応</li>
            <li><strong>チェックイン:</strong> 15:00</li>
            <li><strong>チェックアウト:</strong> 10:00</li>
            <li><strong>Wi-Fi接続:</strong> 各客室およびロビーにて無料Wi-Fiをご利用いただけます。<br>
                パスワード: <strong>StellaShine2025</strong></li>
        </ul>

        <h2>館内施設</h2>
        <ul>
            <li><strong>喫煙所:</strong> 1階ロビー奥にございます。</li>
            <li><strong>コインランドリー:</strong> 3階にございます。洗濯機・乾燥機（有料）をご利用ください。洗剤はフロントにて販売しております。</li>
            <li><strong>自動販売機:</strong> 3階、5階、7階にございます。清涼飲料水、アルコール類を取り揃えております。</li>
            <li><strong>製氷機:</strong> 3階、7階にございます。無料でお使いいただけます。</li>
        </ul>

        <p class="note">ご不明な点がございましたら、お気軽にフロントまでお問い合わせください。</p>
        <div class="back-link">
            <a href="javascript:history.back()">前のページに戻る</a>
        </div>
    </div>
    <%@ include file="../jspf/footer.jspf" %>
</body>
</html>
