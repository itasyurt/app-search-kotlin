package org.itasyurt.appsearch.client.api

import org.itasyurt.appsearch.client.domain.Schema

interface SchemaClient {

    fun get(engineName: String): Schema
    fun update(engineName: String, schema: Schema): Schema
}