package io.taptalk.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TapCheckDeleteAccountStateResponse {

	@JsonProperty("canDelete")
	private boolean canDelete;

	@JsonProperty("message")
	private String message;

	public void setCanDelete(boolean canDelete){
		this.canDelete = canDelete;
	}

	public boolean isCanDelete(){
		return canDelete;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}