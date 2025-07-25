package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ApiKeyReader {
    public static String getApiKey() {
        // 1. 環境変数からAPIキーを直接取得するパターン (推奨)
        //    CHATGPT_API_KEYという環境変数に直接APIキーを設定している場合
        String apiKey = System.getenv("CHATGPT_API_KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            return apiKey;
        }

        // 2. 環境変数にJSONパスが指定されている場合、JSONファイルから読み込むパターン
        //    CHATGPT_API_KEY_JSONという環境変数にJSONファイルのパスを設定している場合
        String jsonPath = System.getenv("CHATGPT_API_KEY_JSON");
        if (jsonPath != null && !jsonPath.isEmpty()) {
            try (FileReader reader = new FileReader(jsonPath)) { // try-with-resourcesで確実にクローズ
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject(); // Gson 2.8.8ならこちらがより直接的

                if (obj.has("api_key")) {
                    return obj.get("api_key").getAsString();
                } else {
                    throw new RuntimeException("JSONファイルに 'api_key' キーが見つかりません。");
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("APIキーJSONファイルが見つかりません: " + jsonPath, e);
            } catch (JsonSyntaxException e) {
                throw new RuntimeException("APIキーJSONファイルの形式が不正です。", e);
            } catch (IOException e) {
                throw new RuntimeException("APIキーJSONファイルの読み込みエラー。", e);
            }
        }

        // どちらの方法でもAPIキーが見つからない場合
        throw new RuntimeException("APIキーが見つかりません。環境変数 CHATGPT_API_KEY または CHATGPT_API_KEY_JSON を設定してください。");
    }
}