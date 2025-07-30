package jp.ac.jc21.tcc.AiSystemEngineeringDept.api.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenAiApiResponse {
	private String id;
	private String object;
	private long created;
	private String model;
	private List<Choice> choices;
	private Usage usage;
	private String systemFingerprint;

	private ErrorResponse error;

	public String getId() {
		return id;
	}

	public String getObject() {
		return object;
	}

	public long getCreated() {
		return created;
	}

	public String getModel() {
		return model;
	}

	public List<Choice> getChoices() {
		return choices;
	}

	public Usage getUsage() {
		return usage;
	}

	public String getSystemFingerprint() {
		return systemFingerprint;
	}

	public ErrorResponse getError() {
		return error;
	}

	public static class Usage {
		@SerializedName("prompt_tokens")
		private int promptTokens;

		@SerializedName("completion_tokens")
		private int completionTokens;

		@SerializedName("total_tokens")
		private int totalTokens;

		public int getPromptTokens() {
			return promptTokens;
		}

		public int getCompletionTokens() {
			return completionTokens;
		}

		public int getTotalTokens() {
			return totalTokens;
		}

	}
}