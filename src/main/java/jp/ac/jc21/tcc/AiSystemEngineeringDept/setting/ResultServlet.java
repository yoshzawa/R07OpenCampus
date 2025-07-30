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

@WebServlet("/setting/result")
public class ResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


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
        // ChatServiceHelperを使ってChatServiceインスタンスを生成
        jp.ac.jc21.tcc.AiSystemEngineeringDept.api.ChatService chatService = ChatServiceHelper.createChatServiceFromSession(session);

		String apiResponseContent;
        try {
            // ChatServiceHelperを使ってAPI呼び出しと例外処理をラップ
            apiResponseContent = ChatServiceHelper.callChatServiceApi(
                chatService,
                service -> {
                    try {
                        return service.getChatGPTResponse(userPrompt);
                    } catch (IOException e) {
                        // getChatGPTResponseはIOExceptionをスローするため、ここでラップ
                        throw new RuntimeException(e); // Unchecked例外に変換
                    }
                },
                request,
                "API呼び出し中にエラーが発生しました。"
            );
        } catch (ServletException e) {
            // ChatServiceHelperが投げたServletExceptionをキャッチし、エラーメッセージを応答に設定
            apiResponseContent = (String) request.getAttribute("message"); // ChatServiceHelperで設定されたエラーメッセージを取得
            if (apiResponseContent == null) { // 何らかの理由でメッセージが設定されていなければデフォルト
                apiResponseContent = "エラーが発生しました。詳細はログをご確認ください。";
            }
        }

		request.setAttribute("apiResponse", apiResponseContent);
		request.getRequestDispatcher("/showResult.jsp").forward(request, response);
	}
}