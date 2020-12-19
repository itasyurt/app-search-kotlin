package org.itasyurt.appsearch.client.domain

data class SynonymSet(val id: String, val synonyms: List<String>)
data class ListSynoynmsResponse(val results: List<SynonymSet>, val meta: Map<String, Any?>)