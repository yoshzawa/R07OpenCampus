package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.ac.jc21.tcc.AiSystemEngineeringDept.api.ChatService;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

// "/index.html"と、"/pages/*"のパスパターンを処理する
@WebServlet({ "/index.html", "/pages/*"})
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    // 元のPAGE_PATHS配列の代わりに、キーと値で管理するMapを使用
    // これにより、URLのパスとJSPファイルのパスを分かりやすく紐づけられる
    private static final Map<String, String> PAGE_PATHS_MAP = new HashMap<>();
    static {
        PAGE_PATHS_MAP.put("index", "/WEB-INF/jsp/index.jsp");
        PAGE_PATHS_MAP.put("hotel_info", "/WEB-INF/jsp/hotel_info.jsp");
        PAGE_PATHS_MAP.put("restaurant_breakfast", "/WEB-INF/jsp/restaurant_breakfast.jsp");
        PAGE_PATHS_MAP.put("sightseeing_spots", "/WEB-INF/jsp/sightseeing_spots.jsp");
        PAGE_PATHS_MAP.put("gourmet_shopping", "/WEB-INF/jsp/gourmet_shopping.jsp");
        PAGE_PATHS_MAP.put("transportation", "/WEB-INF/jsp/transportation.jsp");
        PAGE_PATHS_MAP.put("emergency_info", "/WEB-INF/jsp/emergency_info.jsp");
        PAGE_PATHS_MAP.put("services_amenities", "/WEB-INF/jsp/services_amenities.jsp");
        PAGE_PATHS_MAP.put("faq", "/WEB-INF/jsp/faq.jsp");
    }

    // ページ遷移先のキーを番号順に並べた配列
    private static final String[] PAGE_KEYS = {
        "index",                // カテゴリ0: その他
        "hotel_info",           // カテゴリ1: ホテル情報
        "restaurant_breakfast", // カテゴリ2: レストラン・朝食
        "sightseeing_spots",    // カテゴリ3: 観光スポット
        "gourmet_shopping",     // カテゴリ4: グルメ・ショッピング
        "transportation",       // カテゴリ5: 交通機関
        "emergency_info",       // カテゴリ6: 非常時
        "services_amenities",   // カテゴリ7: サービス・備品
        "faq"                   // カテゴリ8: FAQ
    };

    /**
     * GETリクエストを処理し、トップページ、またはパス情報に基づいて指定されたJSPページに遷移します。
     * 「/pages/」から始まるURLは、このメソッドで処理されます。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // リクエストされたパスからパス情報を取得
        String pathInfo = request.getPathInfo();
        String forwardPath = PAGE_PATHS_MAP.get("index"); // デフォルトの遷移先

        // pathInfoがnullでない、かつ、パスの長さが1より大きい（例: "/index"）場合
        if (pathInfo != null && pathInfo.length() > 1) {
            // 先頭の'/'を削除してページ名を取得
            String pageName = pathInfo.substring(1);
            // マップからJSPパスを取得
            String targetJspPath = PAGE_PATHS_MAP.get(pageName);
            if (targetJspPath != null) {
                forwardPath = targetJspPath;
            } else {
                // マップに存在しない場合は404 Not Foundエラー
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested page was not found.");
                return;
            }
        }

        request.getRequestDispatcher(forwardPath).forward(request, response);
    }


    /**
     * POSTリクエストを処理し、ChatGPTからの応答に基づいて適切な案内ページに遷移します。
     * このメソッドは、`/index.html`へのフォーム送信などで呼び出されます。
     */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String userPrompt = request.getParameter("prompt");
		// デフォルトの遷移先をMapから取得
		String forwardPath = PAGE_PATHS_MAP.get("index");

		if (userPrompt == null || userPrompt.trim().isEmpty()) {
			request.setAttribute("message", "質問が入力されていません。");
			request.getRequestDispatcher(forwardPath).forward(request, response);
			return;
		}

		HttpSession session = request.getSession();
		// ChatServiceHelperを使ってChatServiceインスタンスを生成
		ChatService chatService = ChatServiceHelper.createChatServiceFromSession(session);

		int categoryNumber;

		try {
			// ChatServiceHelperを使ってAPI呼び出しと例外処理をラップ
			categoryNumber = ChatServiceHelper.callChatServiceApi(
				chatService,
				service -> {
					try {
						return service.getChatGPTResponseCategory(userPrompt);
					} catch (IOException e) {
						// getChatGPTResponseCategoryはIOExceptionをスローするため、ここでラップ
						throw new RuntimeException(e); // Unchecked例外に変換
					}
				},
				request,
				"カテゴリ分類中にエラーが発生しました。"
			);
		} catch (ServletException e) {
			// ChatServiceHelperが投げたServletExceptionをキャッチし、エラーメッセージを設定
			// forwardPathはデフォルトのまま (index.jsp)
			categoryNumber = 0; // エラー時は0番のページへ
			// 例外メッセージはChatServiceHelper内でrequest.setAttribute("message")に設定済み
		}

		// 取得したカテゴリ番号に基づいて遷移先を決定
		// PAGE_KEYS配列のインデックスとしてカテゴリ番号を使用
		if (categoryNumber >= 0 && categoryNumber < PAGE_KEYS.length) {
			String key = PAGE_KEYS[categoryNumber];
			forwardPath = PAGE_PATHS_MAP.get(key);
			// 0番のカテゴリ（その他）に分類された場合、特別なメッセージを設定
			if (key.equals("index") && request.getAttribute("message") == null) {
				request.setAttribute("message", "ご質問にお答えできるご案内ページのご用意がありませんでした。恐れ入りますが、別の言い方でご質問ください。");
			}
		} else {
			// 予期せぬ数値が返された場合
			if (request.getAttribute("message") == null) { // エラーメッセージが既に設定されていない場合
				request.setAttribute("message", "恐れ入りますが、ご質問の意図を正確に理解できませんでした。別の言葉でお試しいただくか、以下の案内ページをご参照ください。");
			}
			forwardPath = PAGE_PATHS_MAP.get("index"); // デフォルトのトップページへ
		}

		request.getRequestDispatcher(forwardPath).forward(request, response);
	}
}
