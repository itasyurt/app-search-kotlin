package org.itasyurt.appsearch.client.api.impl

import com.fasterxml.jackson.core.type.TypeReference
import org.apache.http.client.methods.CloseableHttpResponse
import org.itasyurt.appsearch.client.api.SchemaClient
import org.itasyurt.appsearch.client.domain.FieldType
import org.itasyurt.appsearch.client.domain.Schema
import org.itasyurt.appsearch.client.domain.mapper

class DefaultSchemaClient(clientSettings: Map<String, Any>) : SchemaClient, DefaultHttpClient(clientSettings) {
    override fun get(engineName: String): Schema {

        val req = createGetRequest(PATH_FORMAT.format(engineName))
        val resp = httpClient.execute(req)
        return parseSchemaResponse(resp)

    }

    override fun update(engineName: String, schema: Schema): Schema {

        val bodyStr = mapper.writeValueAsString(schema.fields)
        val req = createPostRequest(PATH_FORMAT.format(engineName), bodyStr = bodyStr)
        val resp = httpClient.execute(req)
        return parseSchemaResponse(resp)

    }

    private fun parseSchemaResponse(resp: CloseableHttpResponse): Schema {
        return parseResponse(resp) {
            val v = mapper.readTree(resp.entity.content)
            val fields = mapper.convertValue(v, object : TypeReference<Map<String, FieldType>>() {})
            Schema(fields = fields)
        }
    }


    companion object {
        const val PATH_FORMAT = "engines/%s/schema"

    }


}