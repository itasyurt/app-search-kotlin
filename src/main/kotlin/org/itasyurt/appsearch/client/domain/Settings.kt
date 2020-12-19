package org.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.annotation.*


typealias SearchFields = Map<String, SearchWeight>
typealias ResultFields = Map<String, ResultField>
typealias Boosts = Map<String, List<Boost>>


@JsonInclude(JsonInclude.Include.NON_NULL)
data class SearchWeight(val weight: Number? = null)

data class SearchSettings(
        @JsonProperty("search_fields") val searchFields: SearchFields,
        @JsonProperty("result_fields") val resultFields: ResultFields,
        @JsonProperty("boosts") @JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY]) val boosts: Boosts = emptyMap()

)


enum class Operation(@JsonValue val value: String) {
    ADD("add"),
    MULTIPLY("multiply")
}

enum class Function(@JsonValue val value: String) {
    LINEAR("linear"),
    EXPONENTIAL("exponential"),
    LOGARITHMIC("logarithmic")
}

enum class ProximityFunction(@JsonValue val value: String) {
    LINEAR("linear"),
    EXPONENTIAL("exponential"),
    GAUSSIAN("gaussian")
}


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = ValueBoost::class, name = "value"),
        JsonSubTypes.Type(value = FunctionalBoost::class, name = "functional"),
        JsonSubTypes.Type(value = ProximityBoost::class, name = "proximity")
)
abstract class Boost(val type: String)

data class ValueBoost(
        @JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY]) val value: List<Any>,
        @JsonInclude(JsonInclude.Include.NON_NULL) val operation: Operation? = null,
        @JsonInclude(JsonInclude.Include.NON_NULL) val factor: Number? = null) : Boost("value")

data class FunctionalBoost(
        val function: Function,
        @JsonInclude(JsonInclude.Include.NON_NULL) val operation: Operation?,
        @JsonInclude(JsonInclude.Include.NON_NULL) val factor: Number? = null) : Boost("functional")

data class ProximityBoost(
        val center: Any,
        val function: ProximityFunction,
        @JsonInclude(JsonInclude.Include.NON_NULL) val factor: Number? = null) : Boost("proximity")


@JsonInclude(JsonInclude.Include.NON_NULL)
data class Snippet(val size: Int? = null, val fallback: Boolean? = null)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Raw(val size: Int? = null)

data class ResultField(
        @JsonInclude(JsonInclude.Include.NON_NULL) val raw: Raw? = null,
        @JsonInclude(JsonInclude.Include.NON_NULL) val snippet: Snippet? = null

)
