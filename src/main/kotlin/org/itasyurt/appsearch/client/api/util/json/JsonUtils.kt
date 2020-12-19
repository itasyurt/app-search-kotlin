package org.itasyurt.appsearch.client.api.util.json

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import org.apache.http.HttpResponse
import org.itasyurt.appsearch.client.domain.mapper

fun Any.toJson(): String = mapper.writeValueAsString(this)

fun <T : Any> HttpResponse.readValue(clazz: Class<T>): T {

    return mapper.readValue(this.entity.content, clazz)
}

fun JsonNode?.convertToMap(): Map<String, Any> {
    return mapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})
}
