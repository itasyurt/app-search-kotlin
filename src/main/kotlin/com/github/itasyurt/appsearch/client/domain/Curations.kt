package com.github.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Curation(val id: String? = null,
                    val queries: List<String>,
                    val promoted: List<String> = emptyList(),
                    val hidden: List<String> = emptyList())

data class ListCurationsResponse(val results: List<Curation>, val meta: Map<String, Any?>)