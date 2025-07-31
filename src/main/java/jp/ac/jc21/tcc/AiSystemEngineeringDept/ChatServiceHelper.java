package jp.ac.jc21.tcc.AiSystemEngineeringDept;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.ac.jc21.tcc.AiSystemEngineeringDept.api.ChatService;

import java.io.IOException;
import java.util.function.Function;

/**
 * ChatServiceに関連する共通処理（システムプロンプトの取得、ChatServiceのインスタンス生成、API呼び出し時の例外処理）をまとめたヘルパークラスです。
 */
public class ChatServiceHelper {

    // アプリケーション全体でデフォルトとして使用されるカテゴリ分類用のプロンプトをここに配置
    public static final String DEFAULT_CHAT_SERVICE_PROMPT = "館内施設に関する情報であれば1を、レストランに関する情報であれば2を、それ以外は0を返してください。数字1文字だけ返してください。それ以外は返さないでください。" ;



    /**
     * セッションからシステムプロンプトを取得し、それを用いてChatServiceのインスタンスを生成します。
     * セッションにプロンプトが設定されていない場合は、ChatServiceHelperに定義されたデフォルトプロンプトを使用します。
     *
     * @param session 現在のHttpSessionオブジェクト
     * @return 初期化されたChatServiceのインスタンス
     */
    public static ChatService createChatServiceFromSession(HttpSession session) {
        String sessionScript = (String) session.getAttribute("script");
        String currentSystemPrompt;
        if (sessionScript != null && !sessionScript.isEmpty()) {
            currentSystemPrompt = sessionScript;
        } else {
            // セッションに設定がなければ、ChatServiceHelperに定義されたデフォルトのプロンプトを使用
            currentSystemPrompt = DEFAULT_CHAT_SERVICE_PROMPT;
        }
        return new ChatService(currentSystemPrompt);
    }

    /**
     * ChatServiceのAPI呼び出しを実行し、発生しうる共通例外を処理します。
     * 例外が発生した場合、リクエストにエラーメッセージを設定し、ServletExceptionをスローします。
     *
     * @param <T> API呼び出しの戻り値の型
     * @param chatService ChatServiceのインスタンス
     * @param apiCallFunction 実行するChatServiceのAPI呼び出し（Functionとしてラップ）
     * @param request HttpServletRequestオブジェクト（エラーメッセージ設定用）
     * @param defaultErrorMessage エラー時に設定するデフォルトのエラーメッセージ
     * @return API呼び出しの結果
     * @throws ServletException API通信エラーやその他の予期せぬエラーが発生した場合
     */
    public static <T> T callChatServiceApi(
            ChatService chatService,
            Function<ChatService, T> apiCallFunction,
            HttpServletRequest request,
            String defaultErrorMessage) throws ServletException {

        try {
            return apiCallFunction.apply(chatService);
        } catch (IllegalStateException e) {
            System.err.println("ChatServiceの初期化エラー: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("message", "システム設定エラーが発生しました。");
            throw new ServletException(defaultErrorMessage, e);
        } catch (RuntimeException e) {
            System.err.println("APIキーまたはサービスエラー: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("message", "サービス処理中にエラーが発生しました: " + e.getMessage());
            throw new ServletException(defaultErrorMessage, e);
        }
    }
}