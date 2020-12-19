package org.itasyurt.appsearch.client.api

import org.itasyurt.appsearch.client.domain.SearchRequest
import org.itasyurt.appsearch.client.domain.SearchResponse

interface SearchClient {

    fun search(engineName:String, searchRequest: SearchRequest): SearchResponse

    fun multisearch(engineName: String, queries: List<SearchRequest>):List<SearchResponse>
}