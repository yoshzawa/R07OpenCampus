package jp.ac.jc21.tcc.AiSystemEngineeringDept.api;


import com.google.gson.annotations.SerializedName;

public class Choice {
    private int index;
    private Message message; // 新しく定義したMessageクラスを使用

    @SerializedName("finish_reason")
    private String finishReason;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }
}