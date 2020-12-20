package com.github.itasyurt.appsearch.client.api.impl

import org.apache.http.client.methods.CloseableHttpResponse
import com.github.itasyurt.appsearch.api.SearchSettingsClient
import com.github.itasyurt.appsearch.client.api.util.util.json.toJson
import com.github.itasyurt.appsearch.client.domain.SearchSettings

class DefaultSearchSettingsClient(clientSettings: Map<String, Any>) : SearchSettingsClient, DefaultHttpClient(clientSettings) {

    override fun get(engineName: String): SearchSettings {
        val req = createGetRequest(PATH_FORMAT.format(engineName))
        val resp = httpClient.execute(req)
        return parseSearchSettings(resp)
    }

    override fun reset(engineName: String): SearchSettings {
        val req = createPostRequest(RESET_PATH_FORMAT.format(engineName))
        val resp = httpClient.execute(req)
        return parseSearchSettings(resp)
    }

    override fun update(engineName: String, settings: SearchSettings): SearchSettings {
        val req = createPutRequest(PATH_FORMAT.format(engineName), bodyStr = settings.toJson())
        val resp = httpClient.execute(req)
        return parseSearchSettings(resp)
    }

    private fun parseSearchSettings(resp: CloseableHttpResponse): SearchSettings {
        return parseResponseToType(resp, SearchSettings::class.java)
    }


    companion object {
        const val PATH_FORMAT = "engines/%s/search_settings"
        const val RESET_PATH_FORMAT = "engines/%s/search_settings/reset"
    }

}
