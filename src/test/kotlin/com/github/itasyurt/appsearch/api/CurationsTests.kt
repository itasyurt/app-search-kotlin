package com.github.itasyurt.appsearch.api

import com.github.itasyurt.appsearch.client.api.util.createClient
import com.github.itasyurt.appsearch.client.api.util.createEndpoint
import com.github.itasyurt.appsearch.client.api.util.startClientAndServer
import com.github.itasyurt.appsearch.client.domain.Curation
import com.github.itasyurt.appsearch.client.domain.Pagination

import org.junit.Assert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.client.server.ForwardChainExpectation
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpResponse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CurationsTests {

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
    fun testCurationCreated() {
        val client = createClient()
        val engineName = "myEngine"

        val expectedBody = """
              {
                "queries": ["java", "python"],
                "promoted": ["doc1", "doc2"],
                "hidden" :["doc3"]
              }
             """

        mockCurationsEndpoint(engineName = engineName, method = "POST", expectedBody = expectedBody).callback {
            val responseBody = """
                        {"id":"curId"}
            """

            HttpResponse.response(responseBody)
        }

        val curation = Curation(queries = listOf("java", "python"), promoted = listOf("doc1", "doc2"), hidden = listOf("doc3"))
        val curationId = client.curations.create(engineName, curation)
        Assert.assertEquals("curId", curationId)

    }


    @Test
    fun testCurationUpdated() {
        val client = createClient()
        val engineName = "myEngine"

        val expectedBody = """
              {
                "queries": ["java", "python"],
                "promoted": ["doc1", "doc2"],
                "hidden" :["doc3"]
              }
             """

        mockCurationsEndpoint(engineName = engineName, method = "PUT", curationId = "curId", expectedBody = expectedBody).callback {
            val responseBody = """
                        {"id":"curId"}
            """

            HttpResponse.response(responseBody)
        }

        val curation = Curation(queries = listOf("java", "python"), promoted = listOf("doc1", "doc2"), hidden = listOf("doc3"))
        val curationId = client.curations.update(engineName, curationId = "curId", curation = curation)
        Assert.assertEquals("curId", curationId)

    }

    @Test
    fun testCurationsListed() {
        val client = createClient()
        val engineName = "myEngine"

        val expectedBody = """
              {
                 "page": {
                        "size": 20,
                        "current": 2
                }
              }
             """

        mockCurationsEndpoint(engineName = engineName, method = "GET", expectedBody = expectedBody).callback {
            val responseBody = """ {
                            "results": [
                                        {
                                            "queries": ["java", "python"],
                                            "promoted": ["doc1", "doc2"],
                                            "hidden" :["doc3"]
                                        }

                            ],
                            "meta": {}
                        }
            """

            HttpResponse.response(responseBody)
        }

        val curations = client.curations.list(engineName, Pagination(2, 20))

        Assert.assertEquals(2, curations.results[0].queries.size)

    }


    @Test
    fun testCurationGet() {
        val client = createClient()
        val engineName = "myEngine"


        mockCurationsEndpoint(engineName = engineName, method = "GET", curationId = "curId").callback {
            val responseBody = """
                       {
                        "queries": ["java", "python"],
                        "promoted": ["doc1", "doc2"],
                        "hidden" :["doc3"],
                        "id":"curId"
                        }
            """

            HttpResponse.response(responseBody)
        }

        val result = client.curations.get(engineName, curationId = "curId")
        Assert.assertEquals("curId", result.id)

    }

    @Test
    fun testCurationDeleted() {
        val client = createClient()
        val engineName = "myEngine"


        mockCurationsEndpoint(engineName = engineName, method = "DELETE", curationId = "curId").callback {
            val responseBody = """
                       {
                            "deleted": true
                        }
            """

            HttpResponse.response(responseBody)
        }

        val result = client.curations.delete(engineName, curationId = "curId")
        Assert.assertEquals(true, result)

    }


    private fun mockCurationsEndpoint(engineName: String, curationId: String? = null, expectedBody: String? = null, method: String = "GET"): ForwardChainExpectation {

        val path = if (curationId == null) "/api/as/v1/engines/$engineName/curations" else "/api/as/v1/engines/$engineName/curations/$curationId"

        return createEndpoint(path, method, expectedBody = expectedBody)
    }


}