package com.github.itasyurt.appsearch.client.api.impl

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.http.client.methods.CloseableHttpResponse
import com.github.itasyurt.appsearch.client.api.DocumentClient
import com.github.itasyurt.appsearch.client.api.util.util.json.convertToMap
import com.github.itasyurt.appsearch.client.domain.*


class DefaultDocumentClient(clientSettings: Map<String, Any>) : DocumentClient, DefaultHttpClient(clientSettings) {

    override fun create(engineName: String, vararg documents: Document): SaveDocumentResponse {

        val body = mapper.writeValueAsString(documents)

        val req = createPostRequest(PATH_FORMAT.format(engineName), bodyStr = body)
        val resp = httpClient.execute(req)
        return parseSaveDocumentResponse(resp)

    }

    override fun patch(engineName: String, vararg documents: Document): SaveDocumentResponse {
        val body = mapper.writeValueAsString(documents)

        val req = createPatchRequest(PATH_FORMAT.format(engineName), bodyStr = body)
        val resp = httpClient.execute(req)
        return parseSaveDocumentResponse(resp)

    }

    private fun parseSaveDocumentResponse(resp: CloseableHttpResponse): SaveDocumentResponse {
        return parseResponse(resp) {
            val results = parseResponseToType(resp, Array<SaveDocumentResult>::class.java)
            SaveDocumentResponse(results = results.asList())
        }
    }


    override fun delete(engineName: String, vararg docIds: String): DeleteDocumentResponse {
        val body = mapper.writeValueAsString(docIds)
        val req = createDeleteRequestWithBody(PATH_FORMAT.format(engineName), bodyStr = body)
        val resp = httpClient.execute(req)
        return parseResponse(resp) {
            val results = parseResponseToType(resp, Array<DeleteDocumentResult>::class.java)
            DeleteDocumentResponse(results = results.asList())
        }
    }


    override fun list(engineName: String, pagination: Pagination): ListDocumentResponse {

        val req = createGetRequestWithBody(LIST_PATH_FORMAT.format(engineName), mapper.writeValueAsString(mapOf("page" to pagination)))

        val resp = httpClient.execute(req)
        return parseResponseToType(resp, ListDocumentResponse::class.java)

    }

    override fun get(engineName: String, vararg docIds: String): DocumentResponse {
        val body = mapper.writeValueAsString(docIds)
        val req = createGetRequestWithBody(PATH_FORMAT.format(engineName), bodyStr = body)
        val resp = httpClient.execute(req)
        return parseResponse(resp) {
            val resultsArray = mapper.readTree(resp.entity.content) as ArrayNode
            val results = resultsArray.map {
                when (it) {
                    is ObjectNode -> it.convertToMap()
                    else -> null
                }
            }
            DocumentResponse(results = results)
        }
    }


    companion object {
        const val PATH_FORMAT = "engines/%s/documents"
        const val LIST_PATH_FORMAT = "engines/%s/documents/list"
    }

}