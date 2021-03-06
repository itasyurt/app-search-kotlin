package com.github.itasyurt.appsearch.client.api

import com.github.itasyurt.appsearch.client.domain.Curation
import com.github.itasyurt.appsearch.client.domain.ListCurationsResponse
import com.github.itasyurt.appsearch.client.domain.Pagination

interface CurationsClient {

    fun create(engineName: String, curation: Curation): String
    fun update(engineName: String, curationId: String, curation: Curation): String
    fun get(engineName: String, curationId: String): Curation
    fun delete(engineName: String, curationId: String): Boolean
    fun list(engineName: String,pagination: Pagination= Pagination(1,10)): ListCurationsResponse
}