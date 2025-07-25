package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatService {

    private final Gson gson = new Gson();
    private static final String CHAT_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo"; // 使用するモデル

    private final String systemPrompt; // システムプロンプトを保持するフィールド

    /**
     * コンストラクタ: システムプロンプトを指定してChatServiceを初期化します。
     * このプロンプトは、AIへの指示として各API呼び出しに含まれます。
     *
     * @param systemPrompt AIへのシステムプロンプト（指示内容）
     */
    public ChatService(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    /**
     * デフォルトコンストラクタ: システムプロンプトなしでChatServiceを初期化します。
     * この場合、AIへの特定の指示は行われません。
     */
    public ChatService() {
        this.systemPrompt = null; // または空文字列 ""
    }

    /**
     * ChatGPT APIを呼び出し、指定されたメッセージリストに基づいて応答を取得します。
     * これは低レベルのAPI呼び出しメソッドであり、内部でのみ使用されます。
     *
     * @param messages APIに送信するメッセージのリスト (例: [{"role": "system", "content": "..."}, {"role": "user", "content": "..."}])
     * @return ChatGPTからの応答内容の文字列
     * @throws IOException API通信またはJSON処理中にエラーが発生した場合
     */
    private String callChatGptApi(List<Map<String, String>> messages) throws IOException {
        String apiKey = ApiKeyReader.getApiKey(); // ApiKeyReaderからAPIキーを取得

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(CHAT_API_URL);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", DEFAULT_MODEL);
            requestBody.put("messages", messages); // メッセージリストを直接設定

            String jsonRequestBody = gson.toJson(requestBody);
            httpPost.setEntity(new StringEntity(jsonRequestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse apiResponse = httpClient.execute(httpPost)) {
                String jsonString = new String(apiResponse.getEntity().getContent().readAllBytes());

                JsonObject responseJson = JsonParser.parseString(jsonString).getAsJsonObject();

                if (responseJson.has("choices") && responseJson.getAsJsonArray("choices").size() > 0) {
                    JsonObject firstChoice = responseJson.getAsJsonArray("choices").get(0).getAsJsonObject();
                    if (firstChoice.has("message")) {
                        JsonObject messageObject = firstChoice.getAsJsonObject("message");
                        if (messageObject.has("content")) {
                            String content = messageObject.get("content").getAsString();
                            System.out.println("content:" + content); // デバッグ用出力
                            return content;
                        }
                    }
                } else if (responseJson.has("error")) {
                    throw new IOException("ChatGPT APIエラー: " + responseJson.getAsJsonObject("error").toString());
                }
                throw new IOException("ChatGPTからの予期せぬ応答形式です。完全なレスポンス: " + jsonString);

            } catch (JsonSyntaxException e) {
                throw new IOException("APIレスポンスのJSON形式が不正です: " + e.getMessage(), e);
            }

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
     * このインスタンスにシステムプロンプトが設定されていれば、それもAIに送信されます。
     *
     * @param userPrompt ユーザーからの質問プロンプト
     * @return ChatGPTからの応答内容の文字列
     * @throws IOException API通信またはJSON処理中にエラーが発生した場合
     * @throws RuntimeException APIキーの取得に失敗した場合
     */
    public String getChatGPTResponse(String userPrompt) throws IOException {
        List<Map<String, String>> messages = new ArrayList<>();

        // システムプロンプトが設定されていれば追加
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

        // 汎用API呼び出しメソッドを呼び出す
        return callChatGptApi(messages);
    }

    /**
     * 指定されたプロンプトを使用してChatGPT APIと通信し、
     * ユーザーの質問がどのカテゴリに属するかを示す数値を応答として取得します。
     * このメソッドは、このインスタンスに設定されたシステムプロンプトをカテゴリ分類の指示として使用します。
     *
     * @param userPrompt ユーザーからの質問プロンプト
     * @return 質問が属するカテゴリの数値 (0-8)。
     * 0: その他 (トップページへ誘導)
     * 1: ホテル基本情報と館内施設
     * 2: レストラン・朝食会場
     * 3: 観光・主要スポット
     * 4: 観光・グルメ・ショッピング
     * 5: 交通機関
     * 6: 非常時
     * 7: サービス・貸し出し備品
     * 8: よくある質問 (FAQ)
     * @throws IOException API通信またはJSON処理中にエラーが発生した場合
     * @throws RuntimeException APIキーの取得に失敗した場合
     */
    public int getChatGPTResponseCategory(String userPrompt) throws IOException {
        // このメソッドは、インスタンスに設定されたsystemPromptをカテゴリ分類の指示として使用することを前提とします。
        if (this.systemPrompt == null || this.systemPrompt.isEmpty()) {
            throw new IllegalStateException("カテゴリ分類のためのシステムプロンプトが設定されていません。ChatServiceを適切なプロンプトで初期化してください。");
        }

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", this.systemPrompt); // インスタンスのシステムプロンプトを使用
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);
        messages.add(userMessage);

        // 汎用API呼び出しメソッドを呼び出す
        String apiResponseContent = callChatGptApi(messages);

        // APIからの応答を数値に変換
        try {
            int category = Integer.parseInt(apiResponseContent.trim()); // 前後の空白を除去
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
