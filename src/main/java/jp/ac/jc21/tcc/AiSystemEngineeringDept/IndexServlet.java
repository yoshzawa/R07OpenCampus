package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/", "/index.html"}) // ルートパスと/index.htmlの両方でアクセス可能にする
public class IndexServlet extends HttpServlet { // クラス名をChatServletからIndexServletに変更
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

    // ChatServiceのインスタンスを生成 (ユーザー指定のプロンプトを使用)
    private final ChatService chatService = new ChatService("交通機関関係なら5を、それ以外は0を返してください。それ以外は返さないでください。");

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
		request.setCharacterEncoding("UTF-8"); // リクエストの文字エンコーディングをUTF-8に設定

		String userPrompt = request.getParameter("prompt"); // ユーザーの質問を取得
		String forwardPath = PAGE_PATHS[0]; // デフォルトの遷移先はトップページ (PAGE_PATHS[0])

		if (userPrompt == null || userPrompt.trim().isEmpty()) {
            // 質問が空の場合はエラーメッセージを設定し、トップページに戻す
			request.setAttribute("message", "質問が入力されていません。");
            request.getRequestDispatcher(forwardPath).forward(request, response);
			return;
		}

		int categoryNumber;
		String errorMessage = null;

		try {
            // ChatServiceを呼び出してChatGPTからの応答カテゴリ番号を取得
			categoryNumber = chatService.getChatGPTResponseCategory(userPrompt);
		} catch (IOException e) {
            // API通信またはJSON処理のエラー
			System.err.println("ChatGPT API通信エラー: " + e.getMessage());
			e.printStackTrace();
			errorMessage = "ChatGPT APIとの通信中にエラーが発生しました: " + e.getMessage();
            categoryNumber = 0; // エラー時はトップページへ
        } catch (IllegalStateException e) { // ChatServiceでシステムプロンプトが設定されていない場合
            System.err.println("ChatServiceの初期化エラー: " + e.getMessage());
            e.printStackTrace();
            errorMessage = "システム設定エラーが発生しました。";
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
            request.setAttribute("message", "ご質問にお答えできるご案内ページのご用意がありませんでした。恐れ入りますが、別の言い方でご質問ください。");
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
