package org.itasyurt.appsearch.client.api

import org.itasyurt.appsearch.client.domain.Pagination
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
class SynonymsTests {

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
    fun testCreateSynonyms() {
        val client = createClient()
        val engineName = "engineName"


        val expectedBody = """
                {
                    "synonyms": ["js","javascript"]
                }
            """

        mockSynonymEndpoint(engineName, expectedBody = expectedBody, method = "POST").callback {
            val responseBody = """
                            {
                               "synonyms": ["js","javascript"],
                               "id": "syn1"
                            }
                """
            HttpResponse.response(responseBody)
        }


        val synonyms = listOf("js", "javascript")

        val result = client.synonyms.create(engineName, synonyms)

        Assert.assertEquals("syn1", result.id)
        Assert.assertEquals(2, result.synonyms.size)
    }

    @Test
    fun tesListSynonyms() {
        val client = createClient()
        val engineName = "engineName"


        val expectedBody = """
                {
                    "page": {"current":1, "size":12} 
                }
            """

        mockSynonymEndpoint(engineName, expectedBody = expectedBody, method = "GET").callback {
            val responseBody = """ {
                           "results":[ 
                                    {
                                   "synonyms": ["js","javascript"],
                                   "id": "syn1"
                                    }
                                ],
                            "meta" :{}}
                """
            HttpResponse.response(responseBody)
        }



        val result = client.synonyms.list(engineName, pagination = Pagination(1,12))
        Assert.assertEquals(1, result.results.size)
    }


    @Test
    fun testDeleteSynoyms() {

        val client = createClient()
        val engineName = "engineName"


        mockSynonymEndpoint(engineName, synonymSetId = "syn1", method = "DELETE").callback {
            val responseBody = """
                            {
                               "deleted": true
                            }
                """
            HttpResponse.response(responseBody)
        }


        val result = client.synonyms.delete(engineName, synonymSetId = "syn1")

        Assert.assertTrue(result)


    }


    @Test
    fun testUpdateSynonyms() {
        val client = createClient()
        val engineName = "engineName"


        val expectedBody = """
                {
                    "synonyms": ["js","javascript"]
                }
            """

        mockSynonymEndpoint(engineName, synonymSetId = "syn1", expectedBody = expectedBody, method = "PUT").callback {
            val responseBody = """
                            {
                               "synonyms": ["js","javascript"],
                               "id": "syn1"
                            }
                """
            HttpResponse.response(responseBody)
        }


        val synonyms = listOf("js", "javascript")

        val result = client.synonyms.update(engineName, synonymSetId = "syn1", synonyms = synonyms)

        Assert.assertEquals("syn1", result.id)
        Assert.assertEquals(2, result.synonyms.size)
    }

    @Test
    fun testGetSynonyms() {
        val client = createClient()
        val engineName = "engineName"

        mockSynonymEndpoint(engineName, synonymSetId = "syn1", method = "GET").callback {
            val responseBody = """
                            {
                               "synonyms": ["js","javascript"],
                               "id": "syn1"
                            }
                """
            HttpResponse.response(responseBody)
        }

        val result = client.synonyms.get(engineName, synonymSetId = "syn1")

        Assert.assertEquals("syn1", result.id)
        Assert.assertEquals(2, result.synonyms.size)
    }


    private fun mockSynonymEndpoint(engineName: String, synonymSetId: String? = null, expectedBody: String? = null, method: String = "GET"): ForwardChainExpectation {

        val path = if (synonymSetId == null) "/api/as/v1/engines/$engineName/synonyms" else "/api/as/v1/engines/$engineName/synonyms/$synonymSetId"

        return createEndpoint(path, method = method, expectedBody = expectedBody)
    }


}