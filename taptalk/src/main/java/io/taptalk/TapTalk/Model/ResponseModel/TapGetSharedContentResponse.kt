package io.taptalk.TapTalk.Model.ResponseModel

import com.fasterxml.jackson.annotation.JsonProperty

data class TapGetSharedContentResponse(
	@field:JsonProperty("files")
	val files: List<TapSharedMediaItemModel?>? = null,

	@field:JsonProperty("media")
	val media: List<TapSharedMediaItemModel?>? = null,
	
	@field:JsonProperty("links")
	val links: List<TapSharedMediaItemModel?>? = null
)