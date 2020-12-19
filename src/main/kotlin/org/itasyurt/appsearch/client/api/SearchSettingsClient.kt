package org.itasyurt.appsearch.client.api

import org.itasyurt.appsearch.client.domain.SearchSettings

interface SearchSettingsClient {

    fun get(engineName:String):SearchSettings
    fun reset(engineName: String): SearchSettings
    fun update(engineName: String, settings: SearchSettings): SearchSettings
}