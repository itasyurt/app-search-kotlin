package com.github.itasyurt.appsearch.client.api

import com.github.itasyurt.appsearch.client.domain.Schema

interface SchemaClient {

    fun get(engineName: String): Schema
    fun update(engineName: String, schema: Schema): Schema
}