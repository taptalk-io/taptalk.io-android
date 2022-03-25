package io.taptalk.TapTalk.Model.ResponseModel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TapUnstarMessageResponse{

	@JsonProperty("unstarredMessageIDs")
	private List<String> unstarredMessageIDs;

	public void setUnstarredMessageIDs(List<String> unstarredMessageIDs){
		this.unstarredMessageIDs = unstarredMessageIDs;
	}

	public List<String> getUnstarredMessageIDs(){
		return unstarredMessageIDs;
	}
}