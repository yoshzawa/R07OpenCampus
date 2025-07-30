package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.ac.jc21.tcc.AiSystemEngineeringDept.api.ChatService; // import 文はそのまま

import java.io.IOException;

@WebServlet({ "/index.html"})
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    // 遷移先のURLを格納する配列
    private static final String[] PAGE_PATHS = {
        "/WEB-INF/jsp/index2.jsp",                   // 0: その他、またはエラー時
        "/WEB-INF/jsp/hotel_info.jsp",             // 1: ホテル基本情報と館内施設
        "/WEB-INF/jsp/restaurant_breakfast.jsp",   // 2: レストラン・朝食会場
        "/WEB-INF/jsp/sightseeing_spots.jsp",      // 3: 観光・主要スポット
        "/WEB-INF/jsp/gourmet_shopping.jsp",       // 4: 観光・グルメ・ショッピング
        "/WEB-INF/jsp/transportation.jsp",         // 5: 交通機関
        "/WEB-INF/jsp/emergency_info.jsp",         // 6: 非常時
        "/WEB-INF/jsp/services_amenities.jsp",     // 7: サービス・貸し出し備品
        "/WEB-INF/jsp/faq.jsp"                     // 8: よくある質問 (FAQ)
    };

    /**
     * GETリクエストを処理し、トップページに遷移します。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
    }

    /**
     * POSTリクエストを処理し、ChatGPTからの応答に基づいて適切な案内ページに遷移します。
     */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String userPrompt = request.getParameter("prompt");
		String forwardPath = PAGE_PATHS[0]; // デフォルトの遷移先はトップページ

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
            // forwardPathはデフォルトのまま (PAGE_PATHS[0])
            categoryNumber = 0; // エラー時は0番のページへ
            // 例外メッセージはChatServiceHelper内でrequest.setAttribute("message")に設定済み
        }

        // 取得したカテゴリ番号に基づいて遷移先を決定
        if (categoryNumber >= 0 && categoryNumber < PAGE_PATHS.length) {
            forwardPath = PAGE_PATHS[categoryNumber];
            // 0番のカテゴリ（その他）に分類された場合、特別なメッセージを設定
            if (categoryNumber == 0 && request.getAttribute("message") == null) {
                request.setAttribute("message", "ご質問にお答えできるご案内ページのご用意がありませんでした。恐れ入りますが、別の言い方でご質問ください。");
            }
        } else {
            // 予期せぬ数値が返された場合（ChatService側で0に変換されるはずだが、念のため）
            if (request.getAttribute("message") == null) { // エラーメッセージが既に設定されていない場合
                request.setAttribute("message", "恐れ入りますが、ご質問の意図を正確に理解できませんでした。別の言葉でお試しいただくか、以下の案内ページをご参照ください。");
            }
            forwardPath = PAGE_PATHS[0]; // デフォルトのトップページへ
        }

		request.getRequestDispatcher(forwardPath).forward(request, response);
	}
}