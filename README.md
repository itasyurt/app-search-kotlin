![Status](https://github.com/itasyurt/app-search-kotlin/workflows/.github/workflows/clean_build.yml/badge.svg)
# app-search-kotlin
This is an open-source JVM client for [Elastic App Search](https://www.elastic.co/app-search/) implemented in Kotlin.

## Contents

- [Getting started](#getting-started-)
- [Dependencies](#dependencies)
- [Versioning and Compatibility with App Search](#Versioning and Compatibility)
- [Usage](#usage)
- [Running tests](#running-tests)
- [FAQ](#faq-)
- [Contribute](#contribute-)
- [License](#license-)

## Getting Started

To install app-search-kotlin you can add it in your dependencies. Below you can see a Gradle example.

```
 implementation("com.github.itasyurt:app-search-kotlin:$app-search-version")
```

## Dependencies

* Java 11
* Kotlin 1.3

Following Dependencies are used by app-search-kotlin and added in the classpath.
* [Jackson](https://github.com/FasterXML/jackson-module-kotlin)
* [Apache Http Client](https://hc.apache.org/httpcomponents-client-ga/)

## Versioning and Compatibility with App Search

This client is developed and tested against App Search Version 7.8, but should be compatible with any 8.x version.

The suffixes like alpha00X denote the internal versioning 

## Usage

### Instantiating a Client
Documentation assumes tha Elastic App Search is up and running on http://localhost:3002. Following code segment shows how a client can be initialized

```kotlin
val client = DefaultClient(url = "http://localhost:3002", apiKey = "<your_key>")
 
```
DefaultClient is the default implementaion of the Client interface. Each API in App Search  is considered as separate interfaces that can be accessed and called via the client.
You can see the details on the APIs in the following sections.

### Engines

#### Create an Engine
```kotlin
client.engines.create(Engine("f1-drivers"))
```
### Delete an Engine

```kotlin
client.engines.delete("f1-drivers")
```
### Getting Engines

```kotlin
val engine= client.engines.get("f1-drivers")
```

### Listing Engines
```kotlin
val enginesResponse = client.engines.list(Pagination(current = 1, size = 3))
enginesResponse.results.forEach(::println)
```
If you don't pass a pagination parameter, default pagination is current=1, size=20.

### Documents
App Search Documents are considered as Map<String,Any> objects and referred by a typealias.

#### Creating and Patching Documents

```kotlin
val doc1= mapOf(Pair("id","HAM"), Pair("name","Lewis Hamilton"))
val doc2= mapOf(Pair("id","LEC"),Pair("name","Charles Leclerc"))
client.documents.create(engineName="f1-drivers", doc1, doc2)
val doc3 = mapOf(Pair("id","LEC"), Pair("dob",1997))
client.documents.patch(engineName="f1-drivers", doc3)
```
Note that documents are passed as varargs.

#### Getting and Listing Documents
```kotlin
val docs = client.documents.get(engineName = "f1-drivers", "HAM", "BOT", "VER")
docs.results.forEach(::println)

val docList = client.documents.list(engineName = "f1-drivers", Pagination(2,20))
docList.results.forEach(::println)
```

#### Deleting Documents
```kotlin
val deletionResult = client.documents.delete(engineName = "f1-drivers", "GRO")
```

### Search

####  Creating a Search Request
Search requests be creating a SearchRequest object. Following components can be added in a search request:
* Query
* Pagination:
* Grouping
* Facets
* Filters
* Boosts
* Search Fields
* Result Fields

Below you can see a sample search request, for the complete reference you can refer to official App Search Documentation.
```kotlin
val facets = mapOf(
    Pair("dob", listOf(RangeFacet(ranges = listOf(Range(from = null, to = 1990), Range(from = 1991))))),
    Pair("team", listOf(ValueFacet()))
)
val filters = CompositeFilter(
    all= listOf(
        ValueFilter("nationality",values = listOf("GB","DE")),
        RangeFilter("height", from = 168))
)


val request = SearchRequest(query = "Ferrari", facets = facets, filters = filters)
```
#### Performing a Search
Once you create a SearchRequest object, you can use the search client to perform a search.
```kotlin
val req:SearchRequest = ...
val searchResponse = client.search.search(engineName = "f1-drivers",searchRequest = req)
searchResponse.results.forEach(::println)
searchResponse.facets?.forEach(::println)
```
SearchResponse consists of results and facets that you can iterate on along with the metadata information.

#### Performing a Multi-Search
Multiple search requests can be passed to a multisearch request as a list. Response is a list of SearchResponse objects.
```kotlin
val response =client.search.multisearch(engineName = "f1-drivers", listOf(req1, req2))
response.forEach {println(it.results)}
```
### Search Settings

Search Settings is formed by following components:
* Search Fields
* Result Fields
* Boosts

You can see composition of an example SearchSettings object below. Please refer to the official documentation for the comprehensive set of settings.

```kotlin
val fields = mapOf(
    Pair("name", SearchWeight(10)),
    Pair("team", SearchWeight(2)),
    Pair("bio", SearchWeight(0.2))
)

val resultFields = mapOf(
    Pair("name", ResultField()),
    Pair("bio", ResultField(snippet = Snippet(100)))
)
val boosts = mapOf(Pair("name", listOf(ValueBoost(value = listOf("Hamilton")))))
val settings = SearchSettings(searchFields = fields, resultFields = resultFields, boosts = boosts)
```
Client enables create/reset and get operations on SearchSettings as follows:

```kotlin
client.searchSettings.update(engineName = "f1-drivers", settings=settings)
client.searchSettings.reset(engineName = "f1-drivers")
val retrieved = client.searchSettings.get(engineName = "f1-drivers")
```
### Schemas
Client enables programmatic management of Engine Schemas. Schemas are designed as a Map<String, FieldType> . Key is the field name whereas the value is an enum that refers to the App Search Field Types namely:
* Text
* Number
* Date
* Geolocation

In the sample code below you can see how schemas are managed.

```kotlin
// Create
val schema = Schema(
    fields = mapOf(
        Pair("name", FieldType.TEXT),
        Pair("dob", FieldType.DATE)
    )
)
client.schemas.update(engineName = "f1-drivers", schema)
val schema2 = client.schemas.get(engineName = "f1-drivers")
schema2.fields.forEach { println("${it.key} ${it.value}"}
```

### Synonyms
Client facilitates CRUD operations on Synonym Sets as you can see in the example code below:
```kotlin
// Create
val synonymSet= client.synonyms.create(engineName = "f1-drivers", listOf("ferrari","tifosi"))
// Update
client.synonyms.update(engineName = "f1-drivers",synonymSet.id, listOf("ferrari","horse", "tifosi"))
// Get
val retrievedSynonyms = client.synonyms.get(engineName = "f1-drivers", synonymSet.id)
// Delete
client.synonyms.delete(engineName = "f1-drivers", synonymSet.id)
// List(with optional pagination)
val synonymSets= client.synonyms.list(engineName = "f1-drivers")
synonymSets.results.forEach(::println)
```

### Curations

Client facilitates CRUD operations on Curations as you can see in the example code below:
```kotlin
// Create
val curation = Curation(queries = listOf("champion"), promoted = listOf("HAM"), hidden = listOf("RAI"))
val curId = client.curations.create(engineName = "f1-drivers", curation)

// Update
val curation2 = Curation(queries = listOf("champion"), promoted = listOf("HAM"))
client.curations.update(engineName = "f1-drivers", curationId = curId, curation=curation2)

// Get
val retrievedCuration = client.curations.get(engineName = "f1-drivers", curId)
// List(with optional pagination)
val curations = client.curations.list(engineName = "f1-drivers")
// Delete
val deleted = client.curations.delete(engineName = "f1-drivers", curId)
```

### Suggestions

Client facilitates search suggestion passing a query prefix along with field names. You can see an example below:

```kotlin
val sug = client.suggestions.suggest(engineName = "f1-drivers", SuggestionsRequest(query = "fer", fields = listOf("team", "name")))
sug.suggestions.forEach(::println)
```

