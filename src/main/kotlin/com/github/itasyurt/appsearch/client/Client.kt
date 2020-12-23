package com.github.itasyurt.appsearch.client

import com.github.itasyurt.appsearch.client.api.*

interface Client {

    val engines: EngineClient
    val documents: DocumentClient
    val schemas: SchemaClient
    val searchSettings: SearchSettingsClient
    val search: SearchClient
    val suggestions: SuggestionsClient
    val synonyms: SynonymsClient
    val curations: CurationsClient
}