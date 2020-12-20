package com.github.itasyurt.appsearch.api

import com.github.itasyurt.appsearch.client.domain.Engine
import com.github.itasyurt.appsearch.client.domain.EnginesResponse
import com.github.itasyurt.appsearch.client.domain.Pagination

interface EngineClient {

    fun create(engine: Engine): Engine
    fun delete(name: String): Boolean
    fun get(name: String): Engine
    fun list(pagination: Pagination = Pagination(current = 1, size = 20)): EnginesResponse

}