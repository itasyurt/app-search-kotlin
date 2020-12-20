package com.github.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize



typealias FacetResults = Map<String, List<FacetResult>>
typealias Facets = Map<String, List<Facet>>


abstract class Facet(val type: String, val name: String? = null)


enum class FacetSortKey(@JsonValue val value: String) {
    COUNT("count"),
    VALUE("value")
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class ValueFacet(name: String? = null,
                 val size: Int = 10,
                 @JsonSerialize(using = FacetSortSerializer::class)
                 val sort: FacetSort? = null) : Facet(type = "value", name = name)

data class FacetSort(val key: FacetSortKey, val type: SortType)

@JsonInclude(JsonInclude.Include.NON_NULL)
class RangeFacet(name: String? = null, val ranges: List<Range>) : Facet(type = "range", name = name)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Range(val from: Any?, val to: Any? = null, val name: String? = null)


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = ValueFacetResult::class, name = "value"),
        JsonSubTypes.Type(value = RangeFacetResult::class, name = "range")
)
abstract class FacetResult(val name: String? = null, val type: String)

class ValueFacetResult(name: String? = null, val data: List<ValueFacetData>) : FacetResult(type = "value", name = name)

class ValueFacetData(val count: Number, val value: Any)

class RangeFacetResult(name: String? = null, val data: List<RangeFacetData>) : FacetResult(type = "range", name = name)

class RangeFacetData(val name: String? = null, val count: Number, val from: Any? = null, val to: Any? = null)


