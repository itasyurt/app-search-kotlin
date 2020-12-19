package org.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.util.*


class SortSerializer : JsonSerializer<Pair<String, SortType>>() {

    override fun serialize(value: Pair<String, SortType>, gen: JsonGenerator, serializers: SerializerProvider) {
        with(gen) {
            writeStartObject()
            writeStringField(value.first, value.second.value)
            writeEndObject()
        }
    }
}

class FacetSortSerializer : JsonSerializer<FacetSort>() {

    override fun serialize(value: FacetSort, gen: JsonGenerator, serializers: SerializerProvider) {
        with(gen) {
            writeStartObject()
            writeStringField(value.key.value, value.type.value)
            writeEndObject()
        }
    }
}


abstract class AtomicFilterSerializer<T : AtomicFilter> : JsonSerializer<T>() {
    override fun serialize(value: T, gen: JsonGenerator, serializers: SerializerProvider) {
        with(gen) {
            writeStartObject()
            writeObjectField(value.fieldName, convertAtomicFilter(gen, value))
            writeEndObject()
        }
    }

    abstract fun convertAtomicFilter(gen: JsonGenerator, value: T): Any

}


class ValueFilterSerializer : AtomicFilterSerializer<ValueFilter>() {
    override fun convertAtomicFilter(gen: JsonGenerator, value: ValueFilter): Any {
        return value.values
    }
}

class RangeFilterSerializer: AtomicFilterSerializer<RangeFilter>() {
    override fun convertAtomicFilter(gen: JsonGenerator, value: RangeFilter): Map<String,Any> {
        val  fields = mutableMapOf<String,Any>()
        if(value.from!=null)  {
            fields["from"] = value.from
        }
        if (value.to!=null)  {
            fields["to"]= value.to
        }
        return Collections.unmodifiableMap(fields)
    }

}


class GeolocationFilterSerializer: AtomicFilterSerializer<GeolocationFilter>() {
    override fun convertAtomicFilter(gen: JsonGenerator, value: GeolocationFilter): Map<String,Any> {
        val  fields = mutableMapOf<String,Any>()
        if(value.from!=null)  {
            fields["from"] = value.from
        }
        if (value.to!=null)  {
            fields["to"]= value.to
        }
        if(value.distance!=null) {
            fields["distance"]= value.distance
        }

        fields["center"]= value.center
        fields["unit"]= value.unit

        return Collections.unmodifiableMap(fields)
    }

}

