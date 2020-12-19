package org.itasyurt.appsearch.client.api

import org.itasyurt.appsearch.client.domain.SuggestionsRequest
import org.itasyurt.appsearch.client.api.util.*
import org.junit.Assert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.client.server.ForwardChainExpectation
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpResponse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SuggestionTests {

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
    fun testSuggestionsWithFields() {
        val client = createClient()
        val engineName = "engineName"

        val request = SuggestionsRequest(query = "java", fields = listOf("name"), size = 5)
        val expectedBody = """
                {
                    "query": "java",
                    "types": {"documents": {"fields":["name"]}},
                    "size" : 5
                }
            """

        mockSuggestionEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": {
                                    "documents": [
                                        {"suggestion": "javascript"},
                                        {"suggestion": "java"}
                                    ]
                                },
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }

        val results = client.suggestions.suggest(engineName = engineName, suggestionsRequest = request)
        Assert.assertEquals("javascript", results.suggestions[0])
    }

    @Test
    fun testSuggestions() {
        val client = createClient()
        val engineName = "engineName"


        val expectedBody = """
                {
                    "query": "java"
                }
            """

        mockSuggestionEndpoint(engineName, expectedBody = expectedBody).callback {
            val responseBody = """
                            {
                                "results": {
                                    "documents": [
                                        {"suggestion": "javascript"},
                                        {"suggestion": "java"}
                                    ]
                                },
                                "meta": {}
                            }
                """
            HttpResponse.response(responseBody)
        }
        val request = SuggestionsRequest(query = "java")
        val results = client.suggestions.suggest(engineName = engineName, suggestionsRequest = request)
        Assert.assertEquals("javascript", results.suggestions[0])
    }


    private fun mockSuggestionEndpoint(engineName: String, expectedBody: String?): ForwardChainExpectation {

        return createEndpoint("/api/as/v1/engines/$engineName/query_suggestion", "GET", expectedBody=expectedBody)
    }




}