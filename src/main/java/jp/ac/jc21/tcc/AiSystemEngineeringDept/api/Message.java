package jp.ac.jc21.tcc.AiSystemEngineeringDept.api;

import com.google.gson.annotations.SerializedName;

// Recordとして宣言
public record Message(String role, String content) {
    // Recordの場合、コンストラクタ、ゲッター、equals(), hashCode(), toString() は自動生成されます。
    // Gsonのアノテーションが必要な場合は、フィールド宣言ではなく、
    // コンパクトコンストラクタ内で考慮することもできますが、
    // Recordコンポーネント名とJSONキーが一致していれば通常は不要です。
    // 例: public Message(@SerializedName("role") String role, @SerializedName("content") String content) {}
}
