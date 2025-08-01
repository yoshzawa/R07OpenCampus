<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/jsp_style.css">
    <title>観光・周辺情報（主要スポット） | ホテルステラ仙台駅前</title>
</head>
<body>
<%@ include file="../jspf/header.jspf" %>
    <div class="container">
        <h1>観光・周辺情報（主要スポット）</h1>
        <p>ホテルステラ仙台駅前周辺の主要な観光スポットをご案内いたします。</p>

        <div class="spot-item">
            <h3>仙台城跡 (青葉城址)</h3>
            <p><strong>概要:</strong> 伊達政宗公が築いた仙台城の跡地。現在は護国神社や伊達政宗騎馬像があり、仙台市内を一望できる絶景スポットです。</p>
            <p><strong>アクセス:</strong> ホテルよりバス約20分 (るーぷる仙台バス利用)。</p>
        </div>

        <div class="spot-item">
            <h3>仙台うみの杜水族館</h3>
            <p><strong>概要:</strong> 東日本大震災からの復興を象徴する水族館。三陸の海を再現した巨大水槽や、東北最大級のイルカ・アシカのパフォーマンスが楽しめます。</p>
            <p><strong>アクセス:</strong> 仙台駅からJR仙石線で中野栄駅へ、シャトルバスに乗り換え。</p>
        </div>

        <div class="spot-item">
            <h3>仙台国際センター</h3>
            <p><strong>概要:</strong> コンベンションホールや展示場を備えた多目的施設。青葉山公園内に位置し、国際的な会議やイベントが開催されます。</p>
            <p><strong>アクセス:</strong> ホテルより地下鉄東西線「国際センター駅」下車すぐ。</p>
        </div>

        <div class="spot-item">
            <h3>八木山ベニーランド</h3>
            <p><strong>概要:</strong> 1968年から愛され続ける東北唯一の遊園地。ファミリー向けの乗り物からスリル満点の絶叫マシンまで、幅広いアトラクションが楽しめます。</p>
            <p><strong>アクセス:</strong> 仙台駅より地下鉄東西線「八木山動物公園駅」下車後、徒歩すぐ。</p>
        </div>

        <div class="spot-item">
            <h3>定禅寺通 (じょうぜんじどおり)</h3>
            <p><strong>概要:</strong> ケヤキ並木が美しい仙台を代表するストリート。冬には「SENDAI光のページェント」の会場となります。</p>
            <p><strong>アクセス:</strong> ホテルより徒歩約15分、または地下鉄で一駅。</p>
        </div>

        <div class="back-link">
            <a href="javascript:history.back()">前のページに戻る</a>
        </div>
    </div>
    <%@ include file="../jspf/footer.jspf" %>
</body>
</html>
