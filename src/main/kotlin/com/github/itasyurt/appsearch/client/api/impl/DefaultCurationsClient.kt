package com.github.itasyurt.appsearch.client.api.impl

import org.apache.http.client.methods.CloseableHttpResponse
import com.github.itasyurt.appsearch.api.CurationsClient
import com.github.itasyurt.appsearch.client.api.util.util.json.toJson
import com.github.itasyurt.appsearch.client.domain.Curation
import com.github.itasyurt.appsearch.client.domain.ListCurationsResponse
import com.github.itasyurt.appsearch.client.domain.Pagination
import com.github.itasyurt.appsearch.client.domain.mapper

class DefaultCurationsClient(clientSettings: Map<String, Any>) : CurationsClient, DefaultHttpClient(clientSettings) {

    override fun create(engineName: String, curation: Curation): String {
        val req = createPostRequest(PATH_FORMAT.format(engineName), bodyStr = curation.toJson())
        val resp = httpClient.execute(req)
        return parseCurationSaveResponse(resp)
    }

    override fun update(engineName: String, curationId: String, curation: Curation): String {
        val req = createPutRequest(PATH_FORMAT.format(engineName) + "/$curationId", bodyStr = curation.toJson())
        val resp = httpClient.execute(req)
        return parseCurationSaveResponse(resp)
    }

    override fun get(engineName: String, curationId: String): Curation {
        val req = createGetRequest(PATH_FORMAT.format(engineName) + "/$curationId")
        val resp = httpClient.execute(req)
        return parseResponseToType(resp, Curation::class.java)
    }

    override fun delete(engineName: String, curationId: String): Boolean {
        val req = createDeleteRequest(PATH_FORMAT.format(engineName) + "/$curationId")
        val resp = httpClient.execute(req)
        return parseDeleted(resp)

    }

    override fun list(engineName: String, pagination: Pagination): ListCurationsResponse {
        val bodyStr = mapper.writeValueAsString(mapOf(Pair("page", pagination)))
        val req = createGetRequestWithBody(PATH_FORMAT.format(engineName), bodyStr = bodyStr)
        val resp = httpClient.execute(req)
        return parseResponseToType(resp, ListCurationsResponse::class.java)

    }

    private fun parseCurationSaveResponse(resp: CloseableHttpResponse): String {
        return parseResponse(resp) {
            val responseNode = mapper.readTree(resp.entity.content)
            responseNode["id"].textValue()
        }
    }


    companion object {
        const val PATH_FORMAT = "engines/%s/curations"

    }


}