package jp.ac.jc21.tcc.AiSystemEngineeringDept.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class ApiKeyReader {
    public static Optional<String> getApiKey() {
        // 1. 環境変数からAPIキーを直接取得するパターン (推奨)
        String apiKeyFromEnv = System.getenv("CHATGPT_API_KEY");
        if (apiKeyFromEnv != null && !apiKeyFromEnv.isEmpty()) {
            return Optional.of(apiKeyFromEnv);
        }

        // 2. 環境変数にJSONパスが指定されている場合、JSONファイルから読み込むパターン
        String jsonPath = System.getenv("CHATGPT_API_KEY_JSON");
        if (jsonPath != null && !jsonPath.isEmpty()) {
            try (FileReader reader = new FileReader(jsonPath)) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();

                if (obj.has("api_key")) {
                    String apiKeyFromJson = obj.get("api_key").getAsString();
                    if (apiKeyFromJson != null && !apiKeyFromJson.isEmpty()) {
                        return Optional.of(apiKeyFromJson);
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("APIキーJSONファイルが見つかりません: " + jsonPath);
            } catch (JsonSyntaxException e) {
                System.err.println("APIキーJSONファイルの形式が不正です。");
            } catch (IOException e) {
                System.err.println("APIキーJSONファイルの読み込みエラー。");
            }
        }

        // どちらの方法でもAPIキーが見つからない場合
        return Optional.empty();
    }
}
