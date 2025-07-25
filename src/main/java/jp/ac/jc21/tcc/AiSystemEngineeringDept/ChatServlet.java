package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import java.io.IOException;
// import java.text.ParseException; // 不要になるため削除
// import java.util.HashMap; // 不要になるため削除
// import java.util.Map; // 不要になるため削除

// org.apache.hc.client5.http.classic.methods.HttpPost; // 不要になるため削除
// org.apache.hc.client5.http.impl.classic.CloseableHttpClient; // 不要になるため削除
// org.apache.hc.client5.http.impl.classic.CloseableHttpResponse; // 不要になるため削除
// org.apache.hc.client5.http.impl.classic.HttpClients; // 不要になるため削除
// org.apache.hc.core5.http.ContentType; // 不要になるため削除
// org.apache.hc.core5.http.io.entity.StringEntity; // 不要になるため削除

import com.google.gson.Gson; // レスポンスのJSON変換にGsonを使うなら残す
// import com.google.gson.JsonObject; // 不要になるため削除
// import com.google.gson.JsonParser; // 不要になるため削除

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// private final Gson gson = new Gson(); // ChatServiceに移管されるか、JSPへのJSON渡しも不要なら削除

    // ChatServiceのインスタンスを生成
    private final ChatService chatService = new ChatService();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String userPrompt = request.getParameter("prompt");
		if (userPrompt == null || userPrompt.trim().isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "質問が入力されていません。");
			return;
		}

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