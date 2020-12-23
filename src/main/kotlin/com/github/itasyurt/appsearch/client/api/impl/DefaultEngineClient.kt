package com.github.itasyurt.appsearch.client.api.impl

import com.github.itasyurt.appsearch.client.api.EngineClient
import com.github.itasyurt.appsearch.client.api.util.util.json.toJson
import com.github.itasyurt.appsearch.client.domain.Engine
import com.github.itasyurt.appsearch.client.domain.EnginesResponse
import com.github.itasyurt.appsearch.client.domain.Pagination
import com.github.itasyurt.appsearch.client.domain.mapper

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