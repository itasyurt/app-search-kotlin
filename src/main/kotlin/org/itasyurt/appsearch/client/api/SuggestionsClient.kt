package org.itasyurt.appsearch.client.api

import org.itasyurt.appsearch.client.domain.SuggestionsRequest
import org.itasyurt.appsearch.client.domain.SuggestionsResponse

interface SuggestionsClient {

    fun suggest(engineName:String, suggestionsRequest: SuggestionsRequest): SuggestionsResponse
}