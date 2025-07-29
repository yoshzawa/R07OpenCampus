package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/chat"}) // ルートパスと/chatの両方でアクセス可能にする
public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    // 遷移先のURLを格納する配列
    private static final String[] PAGE_PATHS = {
        "/index.jsp",                   // 0: その他、またはエラー時
        "/hotel_info.html",             // 1: ホテル基本情報と館内施設
        "/restaurant_breakfast.html",   // 2: レストラン・朝食会場
        "/sightseeing_spots.html",      // 3: 観光・主要スポット
        "/gourmet_shopping.html",       // 4: 観光・グルメ・ショッピング
        "/transportation.html",         // 5: 交通機関
        "/emergency_info.html",         // 6: 非常時
        "/services_amenities.html",     // 7: サービス・貸し出し備品
        "/faq.html"                     // 8: よくある質問 (FAQ)
    };

    // カテゴリ分類用のChatServiceインスタンス
    private final ChatService categoryService = new ChatService();

    /**
     * GETリクエストを処理し、トップページに遷移します。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    /**
     * POSTリクエストを処理し、ChatGPTからの応答に基づいて適切な案内ページに遷移します。
     */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); // リクエストの文字エンコーディングをUTF-8に設定

		String userPrompt = request.getParameter("prompt"); // ユーザーの質問を取得
		String forwardPath = "/index.jsp"; // デフォルトの遷移先はトップページ

		if (userPrompt == null || userPrompt.trim().isEmpty()) {
            // 質問が空の場合はエラーメッセージを設定し、トップページに戻す
			request.setAttribute("message", "質問が入力されていません。");
            request.getRequestDispatcher(forwardPath).forward(request, response);
			return;
		}

		int categoryNumber;
		String errorMessage = null;

		try {
            // categoryServiceインスタンスを呼び出してChatGPTからの応答カテゴリ番号を取得
			categoryNumber = categoryService.getChatGPTResponseCategory(userPrompt);
		} catch (IOException e) {
            // API通信またはJSON処理のエラー
			System.err.println("ChatGPT API通信エラー: " + e.getMessage());
			e.printStackTrace();
			errorMessage = "ChatGPT APIとの通信中にエラーが発生しました: " + e.getMessage();
            categoryNumber = 0; // エラー時はトップページへ
		} catch (RuntimeException e) {
            // APIキーの取得エラーなど、getApiKey()からのRuntimeException
            System.err.println("APIキーまたはサービスエラー: " + e.getMessage());
            e.printStackTrace();
            errorMessage = "サービス処理中にエラーが発生しました: " + e.getMessage();
            categoryNumber = 0; // エラー時はトップページへ
        }


        // エラーメッセージがあれば設定
        if (errorMessage != null) {
            request.setAttribute("message", errorMessage);
        }

        // 取得したカテゴリ番号に基づいて遷移先を決定
        // categoryNumberが配列の範囲内であることを確認
        if (categoryNumber >= 0 && categoryNumber < PAGE_PATHS.length) {
            forwardPath = PAGE_PATHS[categoryNumber];
        } else {
            // 予期せぬ数値が返された場合（ChatService側で0に変換されるはずだが、念のため）
            if (errorMessage == null) {
                request.setAttribute("message", "恐れ入りますが、ご質問の意図を正確に理解できませんでした。別の言葉でお試しいただくか、以下の案内ページをご参照ください。");
            }
            forwardPath = PAGE_PATHS[0]; // デフォルトのトップページへ
        }

        // 決定したパスに転送
		request.getRequestDispatcher(forwardPath).forward(request, response);
	}
}
