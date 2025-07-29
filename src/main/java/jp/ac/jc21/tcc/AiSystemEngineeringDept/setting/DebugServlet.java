package jp.ac.jc21.tcc.AiSystemEngineeringDept.setting; // パッケージ名を修正

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet implementation class DebugServlet
 * AIコンシェルジュのシステムプロンプト設定と動作確認を行うためのデバッグサーブレットです。
 */
@WebServlet("/debug")
public class DebugServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    // デフォルトのシステムプロンプト（IndexServletのDEFAULT_CHAT_SERVICE_PROMPTと合わせる）
	static final String DEFAULT_CHAT_SERVICE_PROMPT="必ず0を返してください。それ以外は返さないでください。";


	/**
	 * GETリクエストを処理し、現在のシステムプロンプトを表示するデバッグページにフォワードします。
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String sessionScript = (String) session.getAttribute("script");

        String currentSystemPrompt;
        if (sessionScript != null && !sessionScript.isEmpty()) {
            currentSystemPrompt = sessionScript;
        } else {
            // セッションに設定がなければ、デフォルトのプロンプトを使用
            currentSystemPrompt = DEFAULT_CHAT_SERVICE_PROMPT;
        }
        request.setAttribute("currentSystemPrompt", currentSystemPrompt);
		request.getRequestDispatcher("/input.jsp").forward(request, response); // input.jspからdebug_input.jspに修正
	}

    /**
     * POSTリクエストを処理し、セッションのシステムプロンプトを設定します。
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // リクエストの文字エンコーディングを設定

        String newSystemPrompt = request.getParameter("systemPrompt");
        HttpSession session = request.getSession();

        if (newSystemPrompt != null) {
            session.setAttribute("script", newSystemPrompt);
            request.setAttribute("message", "システムプロンプトがセッションに設定されました。");
        } else {
            request.setAttribute("errorMessage", "システムプロンプトの取得に失敗しました。");
        }
        
        // 設定後、再度doGetを呼び出してデバッグページを再表示
        doGet(request, response);
    }
}
