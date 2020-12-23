package com.github.itasyurt.appsearch.client.api.impl


import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.github.itasyurt.appsearch.client.api.SearchClient
import com.github.itasyurt.appsearch.client.api.util.util.json.convertToMap
import com.github.itasyurt.appsearch.client.api.util.util.json.toJson
import com.github.itasyurt.appsearch.client.domain.*

class DefaultSearchClient(clientSettings: Map<String, Any>) : SearchClient, DefaultHttpClient(clientSettings) {

    override fun search(engineName: String, searchRequest: SearchRequest): SearchResponse {
        val req = createGetRequestWithBody(PATH_FORMAT.format(engineName), bodyStr = searchRequest.toJson())
        val resp = httpClient.execute(req)
        return parseResponse(resp) {
            val responseJson = mapper.readTree(resp.entity.content)

            parseSearchResponse(responseJson)
        }
    }

    private fun parseSearchResponse(responseJson: JsonNode): SearchResponse {
        val resultsArray = responseJson["results"] as ArrayNode
        val results = resultsArray.map { resultNode ->
            parseSearchResult(resultNode)
        }

        val meta = responseJson["meta"].convertToMap()

        val facets = parseFacets(responseJson)

        return SearchResponse(results = results, meta = meta, facets = facets)
    }

    override fun multisearch(engineName: String, queries: List<SearchRequest>): List<SearchResponse> {

        val bodyStr = mapper.writeValueAsString(mapOf("queries" to queries))
        val req = createGetRequestWithBody(MULTI_SEARCH_PATH_FORMAT.format(engineName), bodyStr = bodyStr)

        val resp = httpClient.execute(req)
        return parseResponse(resp) {
            val resultNodes = mapper.readTree(resp.entity.content) as ArrayNode
            resultNodes.map(::parseSearchResponse)

        }

    }

    private fun parseFacets(jsonNode: JsonNode): FacetResults? {
        val facetsNode = jsonNode.get("facets")
        return if (facetsNode == null) {
            null
        } else {
            val facetList = mutableListOf<Pair<String, List<FacetResult>>>()
            facetsNode.fields().forEach { entry ->

                val nodes = entry.value as ArrayNode

                val facetResults = nodes.map { mapper.convertValue(it, FacetResult::class.java) }

                facetList.add(Pair(entry.key, facetResults))
            }
            facetList.toMap()
        }

    }

    private fun parseSearchResult(resultNode: JsonNode): SearchResult {
        val meta = resultNode["_meta"].convertToMap()

        val groupInfo = parseGroupInfo(resultNode)


        val fieldsList = mutableListOf<Pair<String, SearchResultValue>>()
        resultNode.fields().forEach { entry ->
            if (!entry.key.startsWith("_")) {
                fieldsList.add(Pair(entry.key, mapper.convertValue(entry.value, SearchResultValue::class.java)))
            }

        }

        return SearchResult(meta = meta, fields = fieldsList.toMap(), group = groupInfo)
    }

    private fun parseGroupInfo(resultNode: JsonNode): GroupResult? {
        val groupKey = resultNode.get("_group_key")
        return if (groupKey != null) {

            val groupElements = resultNode.get("_group") as ArrayNode

            val results = groupElements.map(::parseSearchResult)

            GroupResult(groupKey = groupKey.textValue(), results = results)
        } else {
            null
        }

    }


    companion object {
        const val PATH_FORMAT = "engines/%s/search"
        const val MULTI_SEARCH_PATH_FORMAT = "engines/%s/multi_search"

    }

}