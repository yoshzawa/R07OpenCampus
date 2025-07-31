package jp.ac.jc21.tcc.AiSystemEngineeringDept.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Optionalをインポート

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jp.ac.jc21.tcc.AiSystemEngineeringDept.api.request.OpenAiApiRequest;
import jp.ac.jc21.tcc.AiSystemEngineeringDept.api.response.OpenAiApiResponse;


public class ChatService {

	private final Gson gson = new Gson();
	private static final String CHAT_API_URL = "https://api.openai.com/v1/chat/completions";
	private static final String DEFAULT_MODEL = "gpt-4o";

    // DEFAULT_CHAT_SERVICE_PROMPT は ChatServiceHelper に移動されました

	private final String systemPrompt;

	public String getSystemPrompt() {
		return systemPrompt;
	}

	/**
	 * コンストラクタ: システムプロンプトを指定してChatServiceを初期化します。
	 *
	 * @param systemPrompt AIへのシステムプロンプト（指示内容）
	 */
	public ChatService(String systemPrompt) {
		this.systemPrompt = systemPrompt;
	}

	/**
	 * デフォルトコンストラクタ: システムプロンプトなしでChatServiceを初期化します。
	 * この場合、空のシステムプロンプトが設定されます。
	 * 通常、このコンストラクタは直接使用せず、ChatServiceHelper.createChatServiceFromSession()などを通じて、
	 * 適切なシステムプロンプトが設定されたChatServiceインスタンスを取得することを推奨します。
	 */
	public ChatService() {
		this(""); // デフォルトで空文字列を設定
	}

	/**
	 * ChatGPT APIを呼び出し、指定されたメッセージリストに基づいて応答を取得します。
	 *
	 * @param requestBody APIに送信するリクエストボディオブジェクト
	 * @return ChatGPTからの応答内容の文字列
	 * @throws IOException API通信またはJSON処理中にエラーが発生した場合
	 */
	private String callChatGptApi(OpenAiApiRequest requestBody) throws IOException {
		// ApiKeyReader.getApiKey() が Optional<String> を返すようになったため修正
		String apiKey = ApiKeyReader.getApiKey()
								  .orElseThrow(() -> new IOException("APIキーが見つかりません。環境変数 CHATGPT_API_KEY または CHATGPT_API_KEY_JSON を設定してください。"));


		HttpClient httpClient = HttpClient.newHttpClient();

		String jsonRequestBody = gson.toJson(requestBody);

		System.out.println("Request JSON: " + jsonRequestBody);

		// HttpRequestを構築
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
		requestBuilder.uri(URI.create(CHAT_API_URL));
		requestBuilder.header("Authorization", "Bearer " + apiKey);
		requestBuilder.header("Content-Type", "application/json");
		HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody)).build();

		try {
			HttpResponse<String> apiResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			String jsonString = apiResponse.body();

			OpenAiApiResponse response = gson.fromJson(jsonString, OpenAiApiResponse.class);

			if (response.getError() != null) {
				throw new IOException("ChatGPT APIエラー: " + response.getError().getError().getMessage());
			} else if (response.getChoices() != null && !response.getChoices().isEmpty()) {
				String content = response.getChoices().get(0).getMessage().getContent();
				System.out.println("content: " + content);
				return content;
			} else {
				throw new IOException("ChatGPTからの予期せぬ応答形式です。完全なレスポンス: " + jsonString);
			}

		} catch (JsonSyntaxException e) {
			throw new IOException("APIレスポンスのJSON形式が不正です: " + e.getMessage(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("API通信が中断されました。", e);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			System.err.println("ChatGPT API通信中に予期せぬエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
			throw new IOException("ChatGPT APIとの通信中に予期せぬエラーが発生しました。", e);
		}
	}

	/**
	 * 指定されたプロンプトを使用してChatGPT APIと通信し、生のテキスト応答を取得します。
	 *
	 * @param userPrompt ユーザーからの質問プロンプト
	 * @return ChatGPTからの応答内容の文字列
	 * @throws IOException API通信またはJSON処理中にエラーが発生した場合
	 */
	public String getChatGPTResponse(String userPrompt) throws IOException {
		List<Message> messages = new ArrayList<>();

		if (this.systemPrompt != null && !this.systemPrompt.isEmpty()) {
			messages.add(new Message("system", this.systemPrompt));
		}

		messages.add(new Message("user", userPrompt));

		OpenAiApiRequest requestBody = new OpenAiApiRequest(DEFAULT_MODEL, messages);
		return callChatGptApi(requestBody);
	}

	/**
	 * 指定されたプロンプトを使用してChatGPT APIと通信し、 ユーザーの質問がどのカテゴリに属するかを示す数値を応答として取得します。
	 * このメソッドの動作には、ChatServiceのインスタンス化時にカテゴリ分類のシステムプロンプトが設定されている必要があります。\n\t *
	 * @param userPrompt ユーザーからの質問プロンプト
	 * @return ChatGPT APIから戻ってきた数値
	 * @throws IOException API通信またはJSON処理中にエラーが発生した場合
	 */
	public int getChatGPTResponseCategory(String userPrompt) throws IOException {
		// システムプロンプトがカテゴリ分類用であるかをより厳密にチェック（必須ではないが、意図しない使用を防ぐため）
		if (this.systemPrompt == null || !this.systemPrompt.contains("カテゴリに分類し") || !this.systemPrompt.contains("数値のみを返してください")) {
			System.err.println("警告: getChatGPTResponseCategoryが意図しないシステムプロンプトで使用されている可能性があります。カテゴリ分類用のプロンプトを推奨します。現在のプロンプト: " + this.systemPrompt);
		}

		List<Message> messages = new ArrayList<>();
		messages.add(new Message("system", this.systemPrompt));
		messages.add(new Message("user", userPrompt));

		OpenAiApiRequest requestBody = new OpenAiApiRequest(DEFAULT_MODEL, messages);
		String apiResponseContent = callChatGptApi(requestBody);

		try {
			int category = Integer.parseInt(apiResponseContent.trim());
			if (category >= 0 && category <= 8) {
				return category;
			} else {
				System.err.println("AIが範囲外の数値を返しました: " + category + ". デフォルトの0を返します。");
				return 0;
			}
		} catch (NumberFormatException e) {
			System.err.println("AIの応答が数値ではありません: '" + apiResponseContent + "'. デフォルトの0を返します。");
			return 0;
		}
	}
}
