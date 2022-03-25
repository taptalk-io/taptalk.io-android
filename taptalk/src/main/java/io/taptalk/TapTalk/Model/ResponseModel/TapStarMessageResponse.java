package io.taptalk.TapTalk.Model.ResponseModel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TapStarMessageResponse{

	@JsonProperty("starredMessageIDs")
	@JsonAlias("messageIDs")
	private List<String> starredMessageIDs;

	public void setStarredMessageIDs(List<String> starredMessageIDs) {
		this.starredMessageIDs = starredMessageIDs;
	}

	public List<String> getStarredMessageIDs(){
		return starredMessageIDs;
	}
}