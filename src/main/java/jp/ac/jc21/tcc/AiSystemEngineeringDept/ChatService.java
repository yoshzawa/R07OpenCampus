package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatService {

    private final Gson gson = new Gson();
    private static final String CHAT_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "gpt-4o";

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
     */
    public ChatService() {
        this.systemPrompt = "何を聞かれても0を返してください";
    }

    /**
     * ChatGPT APIを呼び出し、指定されたメッセージリストに基づいて応答を取得します。
     *
     * @param messages APIに送信するメッセージのリスト
     * @return ChatGPTからの応答内容の文字列
     * @throws IOException API通信またはJSON処理中にエラーが発生した場合
     */
    private String callChatGptApi(List<Map<String, String>> messages) throws IOException {
        String apiKey = ApiKeyReader.getApiKey();

        HttpClient httpClient = HttpClient.newHttpClient();

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", DEFAULT_MODEL);
        requestBodyMap.put("messages", messages);

        String jsonRequestBody = gson.toJson(requestBodyMap);

        System.out.println("Request JSON: " + jsonRequestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CHAT_API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .build();

        try {
            HttpResponse<String> apiResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonString = apiResponse.body();

            JsonObject responseJson = JsonParser.parseString(jsonString).getAsJsonObject();

            if (responseJson.has("choices") && responseJson.getAsJsonArray("choices").size() > 0) {
                JsonObject firstChoice = responseJson.getAsJsonArray("choices").get(0).getAsJsonObject();
                if (firstChoice.has("message")) {
                    JsonObject messageObject = firstChoice.getAsJsonObject("message");
                    if (messageObject.has("content")) {
                        String content = messageObject.get("content").getAsString();
                        System.out.println("content:" + content);
                        return content;
                    }
                }
            } else if (responseJson.has("error")) {
                throw new IOException("ChatGPT APIエラー: " + responseJson.getAsJsonObject("error").toString());
            }
            throw new IOException("ChatGPTからの予期せぬ応答形式です。完全なレスポンス: " + jsonString);

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
        List<Map<String, String>> messages = new ArrayList<>();

        if (this.systemPrompt != null && !this.systemPrompt.isEmpty()) {
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", this.systemPrompt);
            messages.add(systemMessage);
        }

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);
        messages.add(userMessage);

        return callChatGptApi(messages);
    }

    /**
     * 指定されたプロンプトを使用してChatGPT APIと通信し、
     * ユーザーの質問がどのカテゴリに属するかを示す数値を応答として取得します。
     *
     * @param userPrompt ユーザーからの質問プロンプト
     * @return ChatGPT APIから戻ってきた数値
     * @throws IOException API通信またはJSON処理中にエラーが発生した場合
     */
    public int getChatGPTResponseCategory(String userPrompt) throws IOException {
        if (this.systemPrompt == null || this.systemPrompt.isEmpty()) {
            throw new IllegalStateException("カテゴリ分類のためのシステムプロンプトが設定されていません。ChatServiceを適切なプロンプトで初期化してください。");
        }

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", this.systemPrompt);
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);
        messages.add(userMessage);

        String apiResponseContent = callChatGptApi(messages);

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