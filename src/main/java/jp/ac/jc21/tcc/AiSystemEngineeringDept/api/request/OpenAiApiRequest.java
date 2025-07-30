package jp.ac.jc21.tcc.AiSystemEngineeringDept.api.request;

import java.util.List;

import jp.ac.jc21.tcc.AiSystemEngineeringDept.api.Message;

public class OpenAiApiRequest {
	private String model;
	private List<Message> messages;

	public OpenAiApiRequest(String model, List<Message> messages) {
		setModel(model);
		setMessages(messages);
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}