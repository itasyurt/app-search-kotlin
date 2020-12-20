package com.github.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize


typealias Sorts = List<Pair<String, SortType>>

enum class SortType(@JsonValue val value: String) {
    ASC("asc"),
    DESC("desc")
}


@JsonInclude(JsonInclude.Include.NON_NULL)
data class SearchRequest(
        val query: Any,
        val page: Pagination? = Pagination(1, 10),
        @JsonSerialize(contentUsing = SortSerializer::class) val sort: Sorts? = null,
        val group: GroupRequest? = null,
        val facets: Facets? = null,
        val filters: Filter? = null,
        val boosts: Boosts? = null,
        @JsonProperty("search_fields") val searchFields: SearchFields? = null,
        @JsonProperty("result_fields") val resultFields: ResultFields? = null,
        val analytics: Analytics? = null

)

data class Analytics(val tags: List<String>)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class GroupRequest(
        val field: String,
        val size: Int? = null,
        @JsonSerialize(contentUsing = SortSerializer::class) val sort: Sorts? = null
)


data class SearchResponse(val results: List<SearchResult>,
                          val meta: Map<String, Any?>,
                          val facets: FacetResults? = null) {

}

data class SearchResult(private val fields: Map<String, SearchResultValue>, val meta: Map<String, Any>, val group: GroupResult? = null) {

    operator fun get(key: String) = fields.getValue(key)
}

data class GroupResult(val groupKey: String, val results: List<SearchResult>)

data class SearchResultValue(val raw: Any?, val snippet: Any?)