package com.github.itasyurt.appsearch.client.api.impl

import org.apache.http.client.methods.CloseableHttpResponse
import com.github.itasyurt.appsearch.api.SynonymsClient
import com.github.itasyurt.appsearch.client.domain.ListSynoynmsResponse
import com.github.itasyurt.appsearch.client.domain.Pagination
import com.github.itasyurt.appsearch.client.domain.SynonymSet
import com.github.itasyurt.appsearch.client.domain.mapper


class DefaultSynonymsClient(clientSettings: Map<String, Any>) : SynonymsClient, DefaultHttpClient(clientSettings) {
    override fun create(engineName: String, synonyms: List<String>): SynonymSet {
        val bodyStr = mapper.writeValueAsString(mapOf(Pair("synonyms", synonyms)))
        val req = createPostRequest(PATH_FORMAT.format(engineName), bodyStr = bodyStr)
        val resp = httpClient.execute(req)
        return parseSynonymSetResponse(resp)
    }

    override fun get(engineName: String, synonymSetId: String): SynonymSet {
        val path = PATH_FORMAT.format(engineName) + "/$synonymSetId"
        val req = createGetRequest(path)
        val resp = httpClient.execute(req)
        return parseSynonymSetResponse(resp)
    }


    override fun update(engineName: String, synonymSetId: String, synonyms: List<String>): SynonymSet {
        val path = PATH_FORMAT.format(engineName) + "/$synonymSetId"
        val bodyStr = mapper.writeValueAsString(mapOf(Pair("synonyms", synonyms)))
        val req = createPutRequest(path, bodyStr = bodyStr)
        val resp = httpClient.execute(req)
        return parseSynonymSetResponse(resp)

    }

    override fun delete(engineName: String, synonymSetId: String): Boolean {
        val path = PATH_FORMAT.format(engineName) + "/$synonymSetId"
        val req = createDeleteRequest(path)
        val resp = httpClient.execute(req)
        return parseDeleted(resp)
    }

    override fun list(engineName: String, pagination: Pagination): ListSynoynmsResponse {
        val bodyStr = mapper.writeValueAsString(mapOf(Pair("page", pagination)))
        val req = createGetRequestWithBody(PATH_FORMAT.format(engineName), bodyStr = bodyStr)
        val resp = httpClient.execute(req)
        return parseResponseToType(resp, ListSynoynmsResponse::class.java)
    }


    private fun parseSynonymSetResponse(resp: CloseableHttpResponse): SynonymSet {
        return parseResponseToType(resp, SynonymSet::class.java)
    }

    companion object {
        const val PATH_FORMAT = "engines/%s/synonyms"

    }


}