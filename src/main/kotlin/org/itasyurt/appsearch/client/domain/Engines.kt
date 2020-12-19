package org.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Engine(val name: String, val type: String = "default", val language: String? = null)

data class EnginesResponse(val results: List<Engine>, val meta: Map<String, Any?>)