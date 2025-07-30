package jp.ac.jc21.tcc.AiSystemEngineeringDept.setting;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.ac.jc21.tcc.AiSystemEngineeringDept.ChatServiceHelper; // 新しく追加
import jp.ac.jc21.tcc.AiSystemEngineeringDept.api.ChatService;

/**
 * Servlet implementation class DebugServlet
 * AIコンシェルジュのシステムプロンプト設定と動作確認を行うためのデバッグサーブレットです。
 */
@WebServlet("/debug")
public class DebugServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * GETリクエストを処理し、現在のシステムプロンプトを表示するデバッグページにフォワードします。
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        // ChatServiceHelperを使ってシステムプロンプトを取得
        ChatService chatService = ChatServiceHelper.createChatServiceFromSession(session);
        request.setAttribute("currentSystemPrompt", chatService.getSystemPrompt());
		request.getRequestDispatcher("/input.jsp").forward(request, response);
	}

    /**
     * POSTリクエストを処理し、セッションのシステムプロンプトを設定します。
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String newSystemPrompt = request.getParameter("systemPrompt");
        HttpSession session = request.getSession();

        if (newSystemPrompt != null) {
            session.setAttribute("script", newSystemPrompt);
            request.setAttribute("message", "システムプロンプトがセッションに設定されました。");
        } else {
            request.setAttribute("errorMessage", "システムプロンプトの取得に失敗しました。");
        }

        doGet(request, response); // 設定後、再度doGetを呼び出してデバッグページを再表示
    }
}