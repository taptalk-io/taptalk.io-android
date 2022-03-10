package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPUpdateBioRequest{

	@JsonProperty("bio")
	private String bio;

	public String getBio(){
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
}