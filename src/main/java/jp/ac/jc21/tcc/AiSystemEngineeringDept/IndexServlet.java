package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/", "/index.html"}) // ルートパスと/chatの両方でアクセス可能にする
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private final ChatService chatService = new ChatService("0を返してください。それ以外は返さないでください。");

    /**
     * GETリクエストを処理し、トップページまたは指定されたページに遷移します。
     * パラメータがない場合はトップページ (index.jsp) に遷移します。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // パラメータの有無に関わらず、基本的にはindex.jspにフォワード
        // 必要であれば、特定のパラメータに基づいて他のページに飛ばすロジックを追加可能
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
            // ChatServiceを呼び出してChatGPTからの応答カテゴリ番号を取得
			categoryNumber = chatService.getChatGPTResponseCategory(userPrompt);
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
        switch (categoryNumber) {
            case 1:
                forwardPath = "/WEB-INF/jsp//hotel_info.jsp"; // ホテル基本情報と館内施設
                break;
            case 2:
                forwardPath = "/WEB-INF/jsp//restaurant_breakfast.jsp"; // レストラン・朝食会場
                break;
            case 3:
                forwardPath = "/WEB-INF/jsp//sightseeing_spots.jsp"; // 観光・主要スポット
                break;
            case 4:
                forwardPath = "/WEB-INF/jsp//gourmet_shopping.jsp"; // 観光・グルメ・ショッピング
                break;
            case 5:
                forwardPath = "/WEB-INF/jsp//transportation.jsp"; // 交通機関
                break;
            case 6:
                forwardPath = "/WEB-INF/jsp//emergency_info.jsp"; // 非常時
                break;
            case 7:
                forwardPath = "/WEB-INF/jsp//services_amenities.jsp"; // サービス・貸し出し備品
                break;
            case 8:
                forwardPath = "/WEB-INF/jsp/faq.jsp"; // よくある質問 (FAQ)
                break;
            case 0:
            default:
                forwardPath = "/WEB-INF/jsp/index.jsp";
                break;
        }

        // 決定したパスに転送
		request.getRequestDispatcher(forwardPath).forward(request, response);
	}
}
