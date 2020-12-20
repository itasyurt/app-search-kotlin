package com.github.itasyurt.appsearch.api

import com.github.itasyurt.appsearch.client.api.util.createClient
import com.github.itasyurt.appsearch.client.api.util.createEndpoint
import com.github.itasyurt.appsearch.client.api.util.startClientAndServer
import com.github.itasyurt.appsearch.client.domain.Pagination
import com.github.itasyurt.appsearch.client.api.util.util.*
import org.junit.Assert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.client.server.ForwardChainExpectation
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpResponse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentTests {

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
    fun testDocumentsCreated() {
        val client = createClient()
        val engineName = "myEngine"

        val expectedBody = """
             [ {"id":"doc1", name:"jack", "age":19}, {"id":"doc2", name:"joe", "age":21}]
             """

        mockDocumentEndpointWithExpectedBody(engineName = engineName, method = "POST", expectedBody = expectedBody).callback {
            val body = """
                        [
                            {"id":"d1","errors":[]},
                            {"id":"", "errors":["e1", "e2", "e3"]}
                    ]
                """
            HttpResponse.response(body)
        }

        val document1 = mapOf(Pair("id", "doc1"), Pair("name", "jack"), Pair("age", 19))
        val document2 = mapOf(Pair("id", "doc2"), Pair("name", "joe"), Pair("age", 21))


        val result = client.documents.create(engineName, document1, document2)
        Assert.assertEquals(2, result.results.size)
        Assert.assertEquals(3, result.results[1].errors.size)
        Assert.assertTrue(result.results[1].id.isNullOrBlank())

    }


    @Test
    fun testDocumentsGet() {
        val client = createClient()
        val engineName = "myEngine"

        val expectedBody = """
             ["doc1","does_not_exist"]
             """

        mockDocumentEndpointWithExpectedBody(engineName = engineName, method = "GET", expectedBody = expectedBody).callback {

            val bodyStr = """
                        [
                            {"id":"doc1","age":23, "last_updated":"1919-11-19T06:00:00Z" },
                            null
                        ]
             """
            HttpResponse.response(bodyStr)
        }

        val result = client.documents.get(engineName, "doc1", "does_not_exist")
        Assert.assertEquals(2, result.results.size)
        Assert.assertNull(result.results[1])
        Assert.assertEquals(23, result.results[0]!!["age"])

    }

    @Test
    fun testDocumentsList() {
        val client = createClient()
        val engineName = "myEngine"

        val expectedBody = """
             {"page": {"current": 2,"size": 15}}
             """

        mockDocumentListEndpointWithExpectedBody(engineName = engineName, method = "GET", expectedBody = expectedBody).callback {
            val responseBody = """
                { 
                    "results" :[{"id": "doc1", "popularity":4},{"id": "doc2", "popularity":1}],
                    "meta" :{"k":"v"}
                }
                """
            HttpResponse.response(responseBody)
        }

        val result = client.documents.list(engineName, Pagination(2, 15))
        Assert.assertEquals(2, result.results.size)
        Assert.assertEquals(4, result.results[0]["popularity"])
        Assert.assertEquals("v", result.meta["k"])

    }

    @Test
    fun testDocumentsListWithoutPagination() {
        val client = createClient()
        val engineName = "myEngine"

        val expectedBody = """
             {"page": {"current": 1,"size": 100}}
             """

        mockDocumentListEndpointWithExpectedBody(engineName = engineName, method = "GET", expectedBody = expectedBody).callback {
            val responseBody = """
                { 
                    "results" :[{"id": "doc1", "popularity":4},{"id": "doc2", "popularity":1}],
                    "meta" :{"k":"v"}
                }
                """
            HttpResponse.response(responseBody)
        }

       client.documents.list(engineName)


    }


    @Test
    fun testDocumentsDelete() {
        val client = createClient()
        val engineName = "myEngine"

        val expectedBody = """
             ["doc1","doc2","doc3"]
             """

        mockDocumentEndpointWithExpectedBody(engineName = engineName, method = "DELETE", expectedBody = expectedBody).callback {
            val body = """ 
                        [
                            {"id":"doc1", "deleted":true},
                            {"id":"doc2", "deleted":false}
                        ]
                    
                """

            HttpResponse.response(body)
        }

        val result = client.documents.delete(engineName, "doc1", "doc2", "doc3")
        Assert.assertEquals(2, result.results.size)
        Assert.assertEquals(true, result.results[0].deleted)
        Assert.assertEquals(false, result.results[1].deleted)
        Assert.assertEquals("doc2", result.results[1].id)

    }

    @Test
    fun testDocumentsPatch() {
        val client = createClient()

        val document1 = mapOf(Pair("id", "doc1"), Pair("name", "jack"), Pair("age", 19))
        val document2 = mapOf(Pair("name", "joe"), Pair("age", 21))

        val engineName = "myEngine"

        val expectedBody = """
             [ {"id":"doc1", "name":"jack", "age":19}, {"name":"joe", "age":21}]
             """
        mockDocumentEndpointWithExpectedBody(engineName = engineName, method = "PATCH", expectedBody = expectedBody).callback {

            val body = """
                        [
                            {"id":"d1","errors":[]},
                            {"id":"", "errors":["e1", "e2", "e3"]}
                    ]
                """
            HttpResponse.response(body)

        }

        val result = client.documents.patch(engineName, document1, document2)
        Assert.assertEquals(2, result.results.size)
        Assert.assertEquals(3, result.results[1].errors.size)
        Assert.assertTrue(result.results[1].id.isNullOrBlank())


    }

    private fun mockDocumentEndpointWithExpectedBody(engineName: String, method: String = "GET", expectedBody: String): ForwardChainExpectation {
        val path = "/api/as/v1/engines/$engineName/documents"

        return createEndpoint(path, method = method, expectedBody = expectedBody)

    }

    private fun mockDocumentListEndpointWithExpectedBody(engineName: String, method: String = "GET", expectedBody: String): ForwardChainExpectation {
        val path = "/api/as/v1/engines/$engineName/documents/list"

        return createEndpoint(path, method, expectedBody)
    }


}