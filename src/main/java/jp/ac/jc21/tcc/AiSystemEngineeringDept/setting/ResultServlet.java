package jp.ac.jc21.tcc.AiSystemEngineeringDept.setting;

import java.io.IOException;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.ac.jc21.tcc.AiSystemEngineeringDept.ChatService;

@WebServlet("/setting/result")
public class ResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static final String DEFAULT_CHAT_SERVICE_PROMPT="必ず0を返してください。それ以外は返さないでください。";


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String userPrompt = request.getParameter("prompt");
		if (userPrompt == null || userPrompt.trim().isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "質問が入力されていません。");
			return;
		}
        HttpSession session = request.getSession();
        String sessionScript = (String) session.getAttribute("script");

        String currentSystemPrompt;
        if (sessionScript != null && !sessionScript.isEmpty()) {
            currentSystemPrompt = sessionScript;
        } else {
            // セッションに設定がなければ、デフォルトのプロンプトを使用
            currentSystemPrompt = DEFAULT_CHAT_SERVICE_PROMPT;
        }
        ChatService chatService = new ChatService(currentSystemPrompt);
        
		String apiResponseContent;
		try {
            // ChatServiceを呼び出してChatGPTからの応答を取得
			apiResponseContent = chatService.getChatGPTResponse(userPrompt);
		} catch (IOException e) {
			// API通信またはJSON処理のエラー
			System.err.println("ChatGPT API通信エラー: " + e.getMessage());
			e.printStackTrace();
			apiResponseContent = "ChatGPT APIとの通信中にエラーが発生しました: " + e.getMessage();
		} catch (RuntimeException e) {
            // APIキーの取得エラーなど、getApiKey()からのRuntimeException
            System.err.println("APIキーまたはサービスエラー: " + e.getMessage());
            e.printStackTrace();
            apiResponseContent = "サービス処理中にエラーが発生しました: " + e.getMessage();
        }

		// showResult.jsp に結果を転送
		request.setAttribute("apiResponse", apiResponseContent);
		request.getRequestDispatcher("/showResult.jsp").forward(request, response);
	}
}