package org.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize

sealed class Filter()

sealed class AtomicFilter(val fieldName: String) : Filter()

@JsonSerialize(using = ValueFilterSerializer::class)
class ValueFilter(fieldName: String, val values: List<Any>) : AtomicFilter(fieldName = fieldName)

@JsonSerialize(using = RangeFilterSerializer::class)
class RangeFilter(fieldName: String, val from: Any? = null, val to: Any? = null) : AtomicFilter(fieldName = fieldName)

@JsonSerialize(using = GeolocationFilterSerializer::class)
class GeolocationFilter(fieldName: String,
                        val center: String,
                        val unit: String,
                        val from: Any? = null,
                        val to: Any? = null,
                        val distance: Any? = null) : AtomicFilter(fieldName = fieldName)

@JsonInclude(JsonInclude.Include.NON_NULL)
class CompositeFilter(val any: List<Filter>? = null, val all: List<Filter>? = null, val none: List<Filter>? = null) : Filter()