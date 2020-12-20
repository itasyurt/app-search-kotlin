@file:Suppress("UNUSED_VARIABLE")

package org.itasyurt.appsearch.client.runner

import com.github.itasyurt.appsearch.client.DefaultClient
import com.github.itasyurt.appsearch.client.domain.*
import com.github.itasyurt.appsearch.client.domain.Function
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.Array
import kotlin.Exception
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.Suppress
import kotlin.let

const val apiKey ="someKey"
fun main() {

    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    val curId = "cur-5fdb9a3f9f58cdb082d0132c"
    try {
        client.curations.list(engineName).results.forEach { println(it) }
    } catch (exc: Exception) {
        exc.printStackTrace()
    }

}

private fun listCurations() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    val curId = "cur-5fdb9a3f9f58cdb082d0132c"
    // client.curations.create(engineName,curation = Curation(queries = listOf("ferrari"), promoted = listOf("LEC"), hidden = listOf("VET"))).let(::println)
    // client.curations.create(engineName,curation = Curation(queries = listOf("monakolu"), promoted = listOf("LEC") )).let(::println)
    // client.curations.create(engineName,curation = Curation(queries = listOf("spinmaster"), promoted = listOf("VET"))).let(::println)
    // client.curations.create(engineName,curation = Curation(queries = listOf("daddy's boy"), promoted = listOf("STR"))).let(::println)
    // client.curations.create(engineName,curation = Curation(queries = listOf("shuey"), promoted = listOf("RIC"))).let(::println)


    client.curations.list(engineName).results.forEach { println(it) }
    (1..3).map { client.curations.list(engineName, Pagination(it, 2)).results }.forEach(::println)
}

private fun listSynonyms() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    client.synonyms.list(engineName).let {
        it.results.forEach { println(it) }
        println(it.meta)
    }
    (1..3).forEach {
        client.synonyms.list(engineName, Pagination(it, 2)).let { resp ->
            resp.results.forEach { println(it) }
            println(resp.meta)
        }
    }
}

private fun getSynonyms() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    val r1 = client.synonyms.create(engineName, listOf("sergio perez", "checo"))
    client.synonyms.get(engineName, r1.id).let {
        println(it)
    }
}

private fun deleteSynonyms() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    val synSetId = "syn-5fdb37b59f58cd3ffcd01325"
    val resp = client.synonyms.delete(engineName, synonymSetId = synSetId)
    println(resp)
}

private fun updateSynonyms() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    val synSetId = "syn-5fdb37b59f58cd3ffcd01325"
    val resp = client.synonyms.update(
        engineName,
        synonymSetId = synSetId,
        synonyms = listOf("pinkpanther", "rp", "racing point")
    )
    println(resp)
}

private fun suggestions() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)

    client.suggestions.suggest(engineName, suggestionsRequest = SuggestionsRequest(query = "ra")).suggestions.forEach {
        println(it)
    }

    client.suggestions.suggest(
        engineName,
        suggestionsRequest = SuggestionsRequest(query = "ra", fields = listOf("name"))
    ).suggestions.forEach {
        println(it)
    }

    client.suggestions.suggest(
        engineName,
        suggestionsRequest = SuggestionsRequest(query = "dsfsdfds", fields = listOf("name"))
    ).suggestions.forEach {
        println(it)
    }
}

private fun analyticTags() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    val req = SearchRequest(query = "ferrari", analytics = Analytics(tags = listOf("app_search_client")))
    client.search.search(engineName, searchRequest = req).let {
        println(it.results)
    }
}

private fun multiSearch() {
    multisearch()
}

private fun multisearch() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)

    listOf("ferrari", "mercedes").map { SearchRequest(query = it) }.let {
        client.search.multisearch(engineName, queries = it)
    }.forEach { println(it.results) }
}

private fun searchFilters() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)

    val myFilter = CompositeFilter(
        all = listOf(ValueFilter("team_nationality", listOf("DE", "IT"))),
        any = listOf(
            RangeFilter("dob", to = 1988),
            CompositeFilter(
                all = listOf(
                    ValueFilter("nationality", listOf("CA")),
                    ValueFilter("engine", listOf("Mercedes"))

                )
            )
        )
    )


    val sr = SearchRequest(query = "", filters = myFilter)
    val resp = client.search.search(engineName, searchRequest = sr)
    resp.results.forEach { println(it) }
}

private fun saveF1Drivers() {
    val engineName = "f1drivers2020"

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    //client.engines.create(Engine(engineName))

    val drivers = listOf(
        Driver(
            id = "HAM",
            name = "Lewis Hamilton",
            nationality = "GB",
            team = "Mercedes",
            engine = "Mercedes",
            teamNationality = "DE",
            dob = 1985
        ),
        Driver(
            id = "BOT",
            name = "Valtteri Bottas",
            nationality = "FI",
            team = "Mercedes",
            engine = "Mercedes",
            teamNationality = "DE",
            dob = 1989
        ),
        Driver(
            id = "VET",
            name = "Sebastian Vettel",
            nationality = "DE",
            team = "Ferrari",
            engine = "Ferrari",
            teamNationality = "IT",
            dob = 1987
        ),
        Driver(
            id = "LEC",
            name = "Charles Leclerc",
            nationality = "MC",
            team = "Ferrari",
            engine = "Ferrari",
            teamNationality = "IT",
            dob = 1997
        ),
        Driver(
            id = "VER",
            name = "Max Verstappen",
            nationality = "NL",
            team = "Red Bull Racing",
            engine = "Honda",
            teamNationality = "AT",
            dob = 1997
        ),
        Driver(
            id = "ALB",
            name = "Alex Albon",
            nationality = "TH",
            team = "Red Bull Racing",
            engine = "Honda",
            teamNationality = "AT",
            dob = 1996
        ),
        Driver(
            id = "SAI",
            name = "Carlos Sainz Jr.",
            nationality = "ES",
            team = "McLaren",
            engine = "Renault",
            teamNationality = "GB",
            dob = 1994
        ),
        Driver(
            id = "NOR",
            name = "Lando Norris",
            nationality = "GB",
            team = "McLaren",
            engine = "Renault",
            teamNationality = "GB",
            dob = 1999
        ),
        Driver(
            id = "RIC",
            name = "Daniel Ricciardo",
            nationality = "AU",
            team = "Renault",
            engine = "Renault",
            teamNationality = "FR",
            dob = 1989
        ),
        Driver(
            id = "OCO",
            name = "Esteban Ocon",
            nationality = "FR",
            team = "Renault",
            engine = "Renault",
            teamNationality = "FR",
            dob = 1996
        ),
        Driver(
            id = "GAS",
            name = "Pierre Gasly",
            nationality = "FR",
            team = "Alpha Tauri",
            engine = "Honda",
            teamNationality = "IT",
            dob = 1996
        ),
        Driver(
            id = "KVY",
            name = "Daniil Kvyat",
            nationality = "RU",
            team = "Alpha Tauri",
            engine = "Honda",
            teamNationality = "IT",
            dob = 1994
        ),
        Driver(
            id = "PER",
            name = "Sergio Perez",
            nationality = "MX",
            team = "Racing Point",
            engine = "Mercedes",
            teamNationality = "GB",
            dob = 1990
        ),
        Driver(
            id = "STR",
            name = "Lance Stroll",
            nationality = "CA",
            team = "Racing Point",
            engine = "Mercedes",
            teamNationality = "GB",
            dob = 1998
        ),
        Driver(
            id = "RAI",
            name = "Kimi Raikkonen",
            nationality = "FI",
            team = "Alfa Romeo",
            engine = "Ferrari",
            teamNationality = "IT",
            dob = 1979
        ),
        Driver(
            id = "GIO",
            name = "Antonio Giovinazzi",
            nationality = "IT",
            team = "Alfa Romeo",
            engine = "Ferrari",
            teamNationality = "IT",
            dob = 1993
        ),
        Driver(
            id = "MAG",
            name = "Kevin Magnussen",
            nationality = "DK",
            team = "Haas",
            engine = "Ferrari",
            teamNationality = "US",
            dob = 1992
        ),
        Driver(
            id = "GRO",
            name = "Romain Grosjean",
            nationality = "FR",
            team = "Haas",
            engine = "Ferrari",
            teamNationality = "US",
            dob = 1986
        ),
        Driver(
            id = "RUS",
            name = "George Russell",
            nationality = "GB",
            team = "Williams",
            engine = "Mercedes",
            teamNationality = "GB",
            dob = 1998
        ),
        Driver(
            id = "LAT",
            name = "Nicholas Latifi",
            nationality = "CA",
            team = "Williams",
            engine = "Mercedes",
            teamNationality = "GB",
            dob = 1995
        )
    )
    val documents = drivers.map {
        mapOf(
            Pair("id", it.id),
            Pair("name", it.name),
            Pair("nationality", it.nationality),
            Pair("team", it.team),
            Pair("team_nationality", it.teamNationality),
            Pair("engine", it.engine),
            Pair("dob", it.dob)
        )
    }
    client.documents.create(engineName, documents = *documents.toTypedArray())
}

data class Driver(
    val id: String,
    val name: String,
    val nationality: String,
    val team: String,
    val engine: String,
    val teamNationality: String,
    val dob: Int
)

private fun courseFacets() {
    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    val ranges = listOf(Range(from = 0, to = 60, name = "bad"), Range(from = 60))
    val facets = mapOf(
        Pair("rating", listOf(RangeFacet(ranges = ranges))),
        Pair("instructor", listOf(ValueFacet()))
    )
    val searchRequest = SearchRequest("development", facets = facets)
    val results = client.search.search("courses", searchRequest)

    results.results.forEach(::println)
    println("------")
    results.results.filter { it.group != null }.forEach { println(it.group) }
    println("------")
    results.meta.forEach(::println)
}

private fun groupSearch() {
    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    val gr = GroupRequest(field = "instructor")
    val searchRequest = SearchRequest("development", group = gr)
    val results = client.search.search("courses", searchRequest)

    results.results.forEach(::println)
    println("------")
    results.results.filter { it.group != null }.forEach { println(it.group) }
    println("------")
    results.meta.forEach(::println)
}

private fun searchResultWithSort() {
    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    val searchRequest = SearchRequest("development", sort = listOf(Pair("rating", SortType.DESC)))
    val results = client.search.search("courses", searchRequest)

    results.results.forEach(::println)
    println("------")
    results.meta.forEach(::println)
}

private fun updateSettings() {
    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)

    val searchFields = mapOf(
        Pair("headline", SearchWeight(3.0)),
        Pair("title", SearchWeight())
    )
    val resultFields = mapOf(
        Pair("headline", ResultField(raw = Raw(size = 300))),
        Pair("instructor", ResultField(raw = Raw(), snippet = Snippet(size = 120, fallback = true))),
        Pair("rating", ResultField(Raw()))
    )
    val boosts = mapOf(
        Pair("headline", listOf(ValueBoost(value = listOf("java", "python")))),
        Pair(
            "rating", listOf(
                ProximityBoost(center = 65, function = ProximityFunction.LINEAR, factor = 3.0),
                FunctionalBoost(operation = Operation.MULTIPLY, function = Function.LOGARITHMIC, factor = 1)
            )
        )
    )

    val settings = SearchSettings(searchFields = searchFields, resultFields = resultFields, boosts = boosts)
    val result = client.searchSettings.update("courses", settings)
    println(result)
}

private fun schemaSnippet() {
    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)

    val newFields = mapOf(Pair("numeric_field", FieldType.NUMBER))
    val newSchema = Schema(fields = newFields)
    val resultSchema = client.schemas.update("engine1", newSchema)
    println(resultSchema)
}

private fun listDocuments() {
    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)

    client.documents.list("engine1", Pagination(2, 10)).let { result ->
        result.results.forEach(::println)
        result.results.size.let(::println)
        result.meta.let(::println)
    }
}

private fun create50Docs() {
    val d = LocalDateTime.now()

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(Date())

    val client = DefaultClient(url = "http://localhost:3002", apiKey = apiKey)
    try {

        val doc1 = mapOf(Pair("id", 1), Pair("popularity", listOf(23)))

        val docs = Array(50) { i ->
            mapOf(Pair("id", i), Pair("popularity", listOf(i * 5, 23)))

        }

        client.documents.create("engine1", *docs).let(::println)
    } catch (exc: Exception) {

        exc.printStackTrace()
    }
}
