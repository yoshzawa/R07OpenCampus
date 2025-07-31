package jp.ac.jc21.tcc.AiSystemEngineeringDept.api;

public class Message {
    private String role; 
    private String content; 

    // デフォルトコンストラクタ（Gsonがデシリアライズ時に必要）
    public Message() {
    }

    // 全フィールドを引数にとるコンストラクタ
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // Getters
    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    // Setters（Gsonがデシリアライズ時に必要）
    public void setRole(String role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
