package org.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.annotation.JsonValue

data class Schema(val fields: Map<String, FieldType>)

enum class FieldType(@JsonValue val value: String) {
    TEXT("text"),
    NUMBER("number"),
    GEOLOCATION("geolocation"),
    DATE("date")
}