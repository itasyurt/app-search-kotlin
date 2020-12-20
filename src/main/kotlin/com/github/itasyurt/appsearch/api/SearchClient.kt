package com.github.itasyurt.appsearch.api

import com.github.itasyurt.appsearch.client.domain.SearchRequest
import com.github.itasyurt.appsearch.client.domain.SearchResponse

interface SearchClient {

    fun search(engineName:String, searchRequest: SearchRequest): SearchResponse

    fun multisearch(engineName: String, queries: List<SearchRequest>):List<SearchResponse>
}