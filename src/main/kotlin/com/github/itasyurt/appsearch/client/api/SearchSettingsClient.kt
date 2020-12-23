package com.github.itasyurt.appsearch.client.api

import com.github.itasyurt.appsearch.client.domain.SearchSettings

interface SearchSettingsClient {

    fun get(engineName:String):SearchSettings
    fun reset(engineName: String): SearchSettings
    fun update(engineName: String, settings: SearchSettings): SearchSettings
}