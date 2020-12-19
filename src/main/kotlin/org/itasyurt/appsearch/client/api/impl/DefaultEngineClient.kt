package org.itasyurt.appsearch.client.api.impl

import org.itasyurt.appsearch.client.api.EngineClient
import org.itasyurt.appsearch.client.api.util.json.toJson
import org.itasyurt.appsearch.client.domain.Engine
import org.itasyurt.appsearch.client.domain.EnginesResponse
import org.itasyurt.appsearch.client.domain.Pagination
import org.itasyurt.appsearch.client.domain.mapper

class DefaultEngineClient(clientSettings: Map<String, Any>) : EngineClient, DefaultHttpClient(clientSettings) {

    override fun create(engine: Engine): Engine {
        val request = createPostRequest("engines", bodyStr = engine.toJson())
        val resp = httpClient.execute(request)
        return parseResponseToType(resp, Engine::class.java)

    }


    override fun delete(name: String): Boolean {
        val request = createDeleteRequest("engines/$name")

        val resp = httpClient.execute(request)
        return parseDeleted(resp)

    }

    override fun get(name: String): Engine {
        val request = createGetRequest("engines/$name")
        val resp = httpClient.execute(request)
        return parseResponseToType(resp, Engine::class.java)

    }

    override fun list(pagination: Pagination): EnginesResponse {
        val request = createGetRequestWithBody("engines", mapper.writeValueAsString(mapOf("page" to pagination)))
        val resp = httpClient.execute(request)
        return parseResponseToType(resp, EnginesResponse::class.java)

    }
}