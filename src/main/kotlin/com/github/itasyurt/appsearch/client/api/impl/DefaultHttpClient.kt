package com.github.itasyurt.appsearch.client.api.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import org.apache.http.HttpResponse
import org.apache.http.client.methods.*
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import com.github.itasyurt.appsearch.client.api.util.http.HttpDeleteRequestWithBody
import com.github.itasyurt.appsearch.client.api.util.http.HttpGetRequestWithBody
import com.github.itasyurt.appsearch.client.api.util.http.bearer
import com.github.itasyurt.appsearch.client.api.util.http.isSuccess
import com.github.itasyurt.appsearch.client.api.util.util.json.readValue
import com.github.itasyurt.appsearch.client.domain.AppSearchException
import com.github.itasyurt.appsearch.client.domain.mapper

abstract class DefaultHttpClient(clientSettings: Map<String, Any>) {
    val apiKey: String by clientSettings
    val baseUrl: String by clientSettings

    protected val httpClient = HttpClients.createDefault()!!


    protected fun <T> parseResponse(response: HttpResponse, successCallback: () -> T): T {
        if (response.isSuccess()) {
            return successCallback()
        } else {
            val respString = String(response.entity.content.readAllBytes())
            val errors = parseErrors(respString)
            throw AppSearchException(response.statusLine.statusCode, respString, errors)
        }

    }

    fun parseErrors(respString: String): List<String>? {
        val jsonTree = mapper.readTree(respString)
        val errorsNode:JsonNode? =jsonTree.get("errors")
        return if(errorsNode!=null) {
            (errorsNode as ArrayNode).map { it.textValue() }
        }else {
            null
        }

    }

    protected fun <T : Any> parseResponseToType(response: HttpResponse, clazz: Class<T>): T {
        return parseResponse(response) {
            response.readValue(clazz)
        }

    }

    protected fun createGetRequest(path: String) = HttpGet("$baseUrl/$path").bearer(apiKey)
    protected fun createDeleteRequest(path: String) = HttpDelete("$baseUrl/$path").bearer(apiKey)
    protected fun createGetRequestWithBody(path: String, bodyStr: String): HttpGetRequestWithBody {
        return HttpGetRequestWithBody("$baseUrl/$path").bearer(apiKey).apply { entity = StringEntity(bodyStr, ContentType.APPLICATION_JSON) }
    }

    protected fun createDeleteRequestWithBody(path: String, bodyStr: String): HttpDeleteRequestWithBody {
        return HttpDeleteRequestWithBody("$baseUrl/$path").bearer(apiKey).apply { entity = StringEntity(bodyStr, ContentType.APPLICATION_JSON) }
    }


    protected fun createPostRequest(path: String, bodyStr: String? = null): HttpPost {

        return HttpPost("$baseUrl/$path").bearer(apiKey).apply {
            if (bodyStr != null) {
                entity = StringEntity(bodyStr, ContentType.APPLICATION_JSON)
            }
        }

    }

    protected fun createPutRequest(path: String, bodyStr: String? = null): HttpPut {

        return HttpPut("$baseUrl/$path").bearer(apiKey).apply {
            if (bodyStr != null) {
                entity = StringEntity(bodyStr, ContentType.APPLICATION_JSON)
            }
        }

    }

    protected fun createPatchRequest(path: String, bodyStr: String): HttpPatch {

        return HttpPatch("$baseUrl/$path").bearer(apiKey).apply { entity = StringEntity(bodyStr, ContentType.APPLICATION_JSON) }

    }

    protected fun parseDeleted(response: HttpResponse): Boolean {
        return parseResponse(response) {
            val responseJson = mapper.readTree(response.entity.content)
            responseJson["deleted"].booleanValue()
        }

    }
}