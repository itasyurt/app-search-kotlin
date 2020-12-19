package org.itasyurt.appsearch.client.api

import org.itasyurt.appsearch.client.domain.*
import org.itasyurt.appsearch.client.api.util.createClient
import org.itasyurt.appsearch.client.api.util.createEndpoint
import org.itasyurt.appsearch.client.api.util.startClientAndServer
import org.junit.Assert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.client.server.ForwardChainExpectation
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpResponse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchTests {

    lateinit var server: ClientAndServer


    @BeforeAll
    fun beforeAll() {
        server = startClientAndServer()
    }

    @AfterAll
    fun afterAll() {
        server.stop()
    }

    @Test
    fun testSearch() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10}
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "meta": {
                                    "warnings": [],
                                    "request_id": "2992570ab570b581ac6c457bddf68835",
                                    "page": {
                                      "total_pages": 1,
                                      "size": 10,
                                      "current": 1,
                                      "total_results": 3
                                    },
                                    "alerts": []
                                    },
                                "results": [
                                        {
                                        "title": {"raw": "item1" ,"snippet": "snippet"},
                                        "_meta": {"score": 3.1, "id":"park_everglades"},
                                        "id": {"raw": "park_everglades"}
                                        },
                                        {
                                        "title": {"raw": "item2" },
                                        "score": {"raw": 10.0 },
                                        "_meta": {"score": 2.1},
                                        "id": {"raw": "park_everglades"}
                                        }
                                ]
                            }
                """
            HttpResponse.response(responseBody)
        }


        val response = client.search.search(engineName = engineName, searchRequest = SearchRequest("java"))

        Assert.assertEquals(2, response.results.size)
        Assert.assertEquals("item1", response.results[0]["title"].raw)
        Assert.assertEquals("snippet", response.results[0]["title"].snippet)
        Assert.assertEquals(10.0, response.results[1]["score"].raw)
        Assert.assertEquals(2.1, response.results[1].meta["score"])


    }


    @Test
    fun testSearchResultMeta() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10}
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "meta": {
                                    "warnings": [],
                                    "request_id": "req_id",
                                    "page": {
                                      "total_pages": 1,
                                      "size": 10,
                                      "current": 1,
                                      "total_results": 3
                                    },
                                    "alerts": []
                                    },
                                "results": [
                                       
                                ]
                            }
                """
            HttpResponse.response(responseBody)
        }


        val response = client.search.search(engineName = engineName, searchRequest = SearchRequest("java"))

        Assert.assertEquals(4, response.meta.size)
        Assert.assertEquals("req_id", response.meta["request_id"])

    }

    @Test
    fun testGroups() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "group": {"field": "name", "size": 5, "sort": [{"title": "desc"}]}
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [
                                        {
                                        "title": {"raw": "item1" ,"snippet": "snippet"},
                                        "_meta": {"score": 3.1, "id":"park_everglades"},
                                        "_group": [
                                            {"title": {"raw": "a1"}, "_meta":{"score":1}},
                                            {"title": {"raw": "b1"}, "_meta":{"score":1}}
                                        ],
                                        "_group_key": "gk",
                                        "id": {"raw": "park_everglades"}
                                        }
                                ],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }

        val groupSorts = listOf(Pair("title", SortType.DESC))
        val searchRequest = SearchRequest("java", group = GroupRequest("name", size = 5, sort = groupSorts))
        val response = client.search.search(engineName = engineName, searchRequest = searchRequest)

        Assert.assertEquals(1, response.results.size)
        Assert.assertEquals("gk", response.results[0].group?.groupKey)
        Assert.assertEquals(2, response.results[0].group?.results?.size)
        Assert.assertEquals("a1", response.results[0].group?.results!![0]["title"]?.raw)
        Assert.assertEquals(1, response.results[0].group?.results!![0].meta["score"])

    }

    @Test
    fun testSorts() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "sort":[ {"title": "asc"}, {"_score": "desc"}]
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [
                                        {
                                        "title": {"raw": "item1" ,"snippet": "snippet"},
                                        "_meta": {"score": 3.1, "id":"park_everglades"},
                                        "id": {"raw": "park_everglades"}
                                        },
                                        {
                                        "title": {"raw": "item2" },
                                        "score": {"raw": 10.0 },
                                        "_meta": {"score": 2.1},
                                        "id": {"raw": "park_everglades"}
                                        }
                                ],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }

        val searchRequest = SearchRequest("java", sort = listOf(Pair("title", SortType.ASC), Pair("_score", SortType.DESC)))

        val response = client.search.search(engineName = engineName, searchRequest = searchRequest)

        Assert.assertEquals(2, response.results.size)

    }


    @Test
    fun testValueFilter() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "filters": {
                            "name": ["java", "python"]
                    }
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [

                                ],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }


        val filters = ValueFilter("name", listOf("java", "python"))
        val searchRequest = SearchRequest("java", filters = filters)
        client.search.search(engineName = engineName, searchRequest = searchRequest)

    }

    @Test
    fun testRangeFilters() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "filters": {
                            "rating": {"from": 10, "to": 125}
                    }
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [

                                ],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }


        val filters = RangeFilter("rating", from = 10, to = 125)
        val searchRequest = SearchRequest("java", filters = filters)
        client.search.search(engineName = engineName, searchRequest = searchRequest)

    }

    @Test
    fun testGeolocationFilters() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "filters": {
                            "location":{
                                "from": 30, 
                                "to":10, 
                                "center": "center_location",
                                "unit" : "km",
                                "distance": 25
                            }
                    }
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [

                                ],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }


        val filters = GeolocationFilter("location", from = 30, to = 10, center = "center_location", unit = "km", distance = 25)

        val searchRequest = SearchRequest("java", filters = filters)
        client.search.search(engineName = engineName, searchRequest = searchRequest)

    }

    @Test
    fun testSearchWithCustomSearchFields() {

        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "search_fields" : { "name": {}}
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }
        val searchFields = mapOf(Pair("name", SearchWeight()))
        client.search.search(engineName, SearchRequest(query = "java", searchFields = searchFields))


    }


    @Test
    fun testSearchWithCustomResultFields() {

        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "result_fields" : { "name": {}}
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }
        val resultFields = mapOf(Pair("name", ResultField()))
        client.search.search(engineName, SearchRequest(query = "java", resultFields = resultFields))


    }


    @Test
    fun testSearchWithAnalyticsTags() {

        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "analytics" : { "tags": ["web"]}
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }
        val analytics = Analytics(tags= listOf("web"))
        client.search.search(engineName, SearchRequest(query = "java", analytics = analytics))


    }


    @Test
    fun testSearchWithCustomBoosts() {

        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "boosts" : { "name": [{"type":"value", "value":["java"]}]}
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }

        val boosts = mapOf(Pair("name", listOf(ValueBoost(value = listOf("java")))))
        client.search.search(engineName, SearchRequest(query = "java", boosts = boosts))


    }


    @Test
    fun testCompositeFilters() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "filters": {
                            "any": [{"rating": {"from": 10, "to": 125}},{"rating": {"from": 200, "to": 300}} ],
                            "all": [{"name": ["java"]}]
                    }
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [

                                ],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }


        val anyFilter = listOf(RangeFilter("rating", from = 10, to = 125), RangeFilter("rating", from = 200, to = 300))
        val allFilter = listOf(ValueFilter("name", listOf("java")))
        val searchRequest = SearchRequest("java", filters = CompositeFilter(all = allFilter, any = anyFilter))
        client.search.search(engineName = engineName, searchRequest = searchRequest)

    }

    @Test
    fun testNestedFilters() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "filters": {
                              "all": [
                                    {"name": ["java"]},
                                    {
                                        "any": [{"rating": {"from": 10, "to": 125}},{"rating": {"from": 200, "to": 300}} ]
                          
                                    }
                            ]
                    }
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [

                                ],
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }


        val anyFilter = listOf(RangeFilter("rating", from = 10, to = 125), RangeFilter("rating", from = 200, to = 300))
        val allFilter = listOf(ValueFilter("name", listOf("java")), CompositeFilter(any = anyFilter))
        val searchRequest = SearchRequest("java", filters = CompositeFilter(all = allFilter))
        client.search.search(engineName = engineName, searchRequest = searchRequest)

    }

    @Test
    fun testRangeFacets() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "query": "java",
                    "page": {"current": 1, "size": 10},
                    "facets": {
                            "rating": [ 
                                {   "type":"range",
                                    "ranges": [
                                        {"from": 0, "to": 50, "name":"bad" },
                                        {"from": 50}
                                    ]
                                }
                            ]
                    }
                }
            """

        mockSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": [

                                ],
                                "meta": {},
                                "facets": {
                                    "rating" :  [
                                                {   "type":"range",
                                                    "data":[
                                                        {"name":"bad", "from":0,  "to": 50, "count":15 },
                                                        {"from":50,  "count":34 }
                                                    ]
                                                }
                                    ]
                                }
                            }
                """
            HttpResponse.response(responseBody)
        }

        val ranges = listOf(Range(from = 0, to = 50, name = "bad"), Range(from = 50))
        val facets = mapOf(Pair("rating", listOf(RangeFacet(ranges = ranges))))
        val searchRequest = SearchRequest("java", facets = facets)
        val response = client.search.search(engineName = engineName, searchRequest = searchRequest)

        Assert.assertEquals(1, response.facets?.size)
        val facetResponse = response.facets!!["rating"]!![0] as RangeFacetResult

        Assert.assertEquals(0, facetResponse.data[0].from)
        Assert.assertEquals(50, facetResponse.data[0].to)
        Assert.assertEquals(15, facetResponse.data[0].count)
        Assert.assertEquals("bad", facetResponse.data[0].name)
        Assert.assertEquals(50, facetResponse.data[1].from)

    }


    @Test
    fun testMultiSearch() {
        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "queries": [
                            {"query": "java",  "page": {"current": 1, "size": 10}}, 
                            {"query": "python", "page": {"current": 1, "size": 10}}
                    ]
                    
                }
            """

        mockMultiSearchEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                        [
                            { "results": [],"meta": {}},
                            { "results": [],"meta": {}}
                        ]
                """
            HttpResponse.response(responseBody)
        }
        val queries = listOf(SearchRequest("java"), SearchRequest("python"))
        val multiResponse = client.search.multisearch(engineName, queries = queries)

        Assert.assertEquals(2, multiResponse.size)

    }


    private fun mockSearchEndpoint(engineName: String, expectedBody: String? = null): ForwardChainExpectation {
        return searchEndpoint("/api/as/v1/engines/$engineName/search", expectedBody)
    }

    private fun mockMultiSearchEndpoint(engineName: String, expectedBody: String? = null): ForwardChainExpectation {
        return searchEndpoint("/api/as/v1/engines/$engineName/multi_search", expectedBody)
    }

    private fun searchEndpoint(targetPath: String, expectedBody: String?): ForwardChainExpectation {

        return createEndpoint(targetPath, "GET", expectedBody)
    }



}