package org.itasyurt.appsearch.client


import org.itasyurt.appsearch.client.api.*
import org.itasyurt.appsearch.client.api.impl.*

class DefaultClient(val url: String, val apiKey: String) : Client {

    private val baseUrl = "$url/api/as/v1"

    private val settings = mapOf(Pair("baseUrl", baseUrl), Pair("apiKey", apiKey))

    override val engines: EngineClient
        get() = DefaultEngineClient(settings)
    override val documents: DocumentClient
        get() = DefaultDocumentClient(settings)
    override val schemas: SchemaClient
        get() = DefaultSchemaClient(settings)
    override val searchSettings:SearchSettingsClient
        get() = DefaultSearchSettingsClient(settings)
    override val search: SearchClient
        get() = DefaultSearchClient(settings)
    override val suggestions: SuggestionsClient
        get() = DefaultSuggestionsClient(settings)
    override val synonyms: SynonymsClient
        get() = DefaultSynonymsClient(settings)
    override val curations: CurationsClient
        get() = DefaultCurationsClient(settings)


}