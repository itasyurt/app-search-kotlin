package com.github.itasyurt.appsearch.client.api

import com.github.itasyurt.appsearch.client.domain.SuggestionsRequest
import com.github.itasyurt.appsearch.client.domain.SuggestionsResponse

interface SuggestionsClient {

    fun suggest(engineName:String, suggestionsRequest: SuggestionsRequest): SuggestionsResponse
}