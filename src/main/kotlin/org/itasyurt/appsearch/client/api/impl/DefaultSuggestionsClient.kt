package org.itasyurt.appsearch.client.api.impl

import com.fasterxml.jackson.databind.JsonNode
import org.itasyurt.appsearch.client.api.SuggestionsClient
import org.itasyurt.appsearch.client.api.util.json.convertToMap
import org.itasyurt.appsearch.client.domain.SuggestionsRequest
import org.itasyurt.appsearch.client.domain.SuggestionsResponse
import org.itasyurt.appsearch.client.domain.mapper

class DefaultSuggestionsClient(clientSettings: Map<String, Any>) : SuggestionsClient, DefaultHttpClient(clientSettings) {
    override fun suggest(engineName: String, suggestionsRequest: SuggestionsRequest): SuggestionsResponse {

        val requestMap = createRequestBodyMap(suggestionsRequest)

        val bodyStr = mapper.writeValueAsString(requestMap)
        val req = createGetRequestWithBody(PATH_FORMAT.format(engineName), bodyStr = bodyStr)
        val resp = httpClient.execute(req)
        return parseResponse(resp) {
            val responseJson = mapper.readTree(resp.entity.content)

            val meta = responseJson["meta"].convertToMap()

            val suggestions = parseSuggestions(responseJson.get("results"))

            SuggestionsResponse(suggestions = suggestions, meta = meta)
        }


    }

    private fun parseSuggestions(results: JsonNode): List<String> {
        return results["documents"].map {
            it["suggestion"].textValue()
        }
    }

    private fun createRequestBodyMap(suggestionsRequest: SuggestionsRequest): Map<String, Any> {
        val requestMap = mutableMapOf<String, Any>()
        requestMap["query"] = suggestionsRequest.query

        if (suggestionsRequest.size != null) {
            requestMap["size"] = suggestionsRequest.size
        }

        if (!suggestionsRequest.fields.isNullOrEmpty()) {
            requestMap["types"] = mapOf(Pair("documents", mapOf(Pair("fields", suggestionsRequest.fields))))
        }
        return requestMap
    }


    companion object {
        const val PATH_FORMAT = "engines/%s/query_suggestion"

    }


}
