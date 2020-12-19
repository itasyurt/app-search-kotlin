package org.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SuggestionsRequest(val query: String, val fields: List<String>? = null, val size: Int? = null)

data class SuggestionsResponse(val suggestions: List<String>, val meta: Map<String, Any?>)