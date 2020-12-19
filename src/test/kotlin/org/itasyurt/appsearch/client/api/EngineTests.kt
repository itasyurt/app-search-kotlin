package org.itasyurt.appsearch.client.api

import com.fasterxml.jackson.databind.JsonNode
import org.itasyurt.appsearch.client.Client
import org.itasyurt.appsearch.client.domain.AppSearchException
import org.itasyurt.appsearch.client.domain.Engine
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
class EngineTests {

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
    fun testCreateEngine() {

        val client: Client = createClient()

        val expectedBody = """{
                "name": "myEngine",
                "type": "default"
            }
            """

        mockEngineEndpoint(method = "POST", expectedBody = expectedBody).callback {
            val respBody = """{
                "name": "dummy",
                "type": "default",
                "language":"en"
            }
            """
            HttpResponse.response(respBody)
        }


        val engine = Engine("myEngine")
        val result = client.engines.create(engine)
        Assert.assertEquals("dummy", result.name)
    }

    @Test
    fun testGetEngine() {

        val client: Client = createClient()
        val name = "enginename"

        mockEngineEndpoint(name).callback {
            val respBody = """{
                "name": "dummy",
                "type": "default",
                "language":"en"
            }
            """
            HttpResponse.response(respBody)
        }

        val result = client.engines.get(name)
        Assert.assertEquals("dummy", result.name)
    }

    @Test
    fun testDeleteEngine() {

        val client: Client = createClient()
        val name = "enginename"

        mockEngineEndpoint("enginename", method = "DELETE").callback {
            val resp = """
                {"deleted":true}
                """
            HttpResponse.response(resp)
        }

        val result = client.engines.delete(name)
        Assert.assertEquals(true, result)
    }


    @Test
    fun testListEngines() {

        val client: Client = createClient()

        val expectedBody = """{
                "page": {"current":1, "size":20}
            }
            """

        mockEngineEndpoint(expectedBody = expectedBody).callback {
            val respBody = """
               {    "results" : [{"name":"e1", "type":"default", "language":"en"}],
                    "meta": {}
            }
            """
            HttpResponse.response(respBody)

        }

        val result = client.engines.list()

        Assert.assertEquals(1, result.results.size)

    }

    @Test
    fun testListEnginesWithParam() {

        val client: Client = createClient()

        val expectedBody = """{
                "page": {"current":2, "size":1}
            }
            """


        mockEngineEndpoint(expectedBody = expectedBody).callback {
            val respBody = """
               {    "results" : [{"name":"e1", "type":"default", "language":"en"}],
                    "meta": {}
            }
            """
            HttpResponse.response(respBody)
        }

        client.engines.list(pagination = Pagination(current = 2, size = 1))

    }

    @Test
    fun testGetEngineReturns404() {

        val client: Client = createClient()
        val name = "enginename"

        mockEngineEndpoint(name).callback {
            HttpResponse.notFoundResponse()
        }

        Assert.assertThrows(AppSearchException::class.java) {
            client.engines.get(name)
        }.let {
            Assert.assertEquals(404, it.statusCode)
        }

    }

    @Test
    fun testCreateEngineReturns400() {

        val client: Client = createClient()

        mockEngineEndpoint(method = "POST").callback {

            HttpResponse().withStatusCode(400).withBody("""
                {"errors":["1","2","3"]}
                """)
        }


        val engine = Engine("myEngine")

        Assert.assertThrows(AppSearchException::class.java) {
            client.engines.create(engine)
        }.let {
            Assert.assertEquals(400, it.statusCode)
        }
    }


    private fun mockEngineEndpoint(engineName: String? = null, method: String = "GET", expectedBody: String? = null): ForwardChainExpectation {

        val path = if (engineName == null) "/api/as/v1/engines" else "/api/as/v1/engines/$engineName"

        return createEndpoint(path, method, expectedBody = expectedBody)
    }


}