package com.github.itasyurt.appsearch.client.api

import com.github.itasyurt.appsearch.client.domain.ListSynoynmsResponse
import com.github.itasyurt.appsearch.client.domain.Pagination
import com.github.itasyurt.appsearch.client.domain.SynonymSet

interface SynonymsClient {

    fun create(engineName:String, synonyms:List<String>): SynonymSet
    fun get(engineName: String, synonymSetId:String): SynonymSet
    fun update(engineName:String, synonymSetId:String, synonyms:List<String>): SynonymSet
    fun delete(engineName: String, synonymSetId: String):Boolean
    fun list(engineName: String, pagination: Pagination=Pagination(1,10)): ListSynoynmsResponse
}