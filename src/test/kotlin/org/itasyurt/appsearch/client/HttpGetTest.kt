package org.itasyurt.appsearch.client


import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import com.github.itasyurt.appsearch.client.domain.mapper
import com.github.itasyurt.appsearch.client.api.util.http.HttpGetRequestWithBody
import org.junit.Assert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.client.server.ForwardChainExpectation
import org.mockserver.client.server.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.Times
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpgetTests {

    lateinit var server: ClientAndServer


    @BeforeAll
    fun beforeAll() {
        server = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)
    }

    @AfterAll
    fun afterAll() {
        server.stop()
    }


    @Test
    fun testListEngines() {

        mockEngineGetEndpoint().callback { it ->
            val obj = mapper.readTree(it.body.rawBytes) as ObjectNode
            val name = obj.get("name").textValue().toUpperCase()
            val respBody = """
                {"name" :"$name"}
                """
            HttpResponse.response(respBody)
        }

        val bodyStr = """
            {"name": "abc"}
            """

        val req = HttpGetRequestWithBody("http://localhost:8000/api/as/v1/engines")
        req.entity = StringEntity(bodyStr, ContentType.APPLICATION_JSON)

        val client = HttpClients.custom().build()

        val resp = client.execute(req)
        val stream = resp.entity.content

        val json = mapper.readTree(stream)

        Assert.assertEquals(200, resp.statusLine.statusCode)
        Assert.assertEquals("ABC", json.get("name").textValue())
        Assert.fail()

    }


    private fun mockEngineGetEndpoint(engineName: String? = null): ForwardChainExpectation {
        return MockServerClient("127.0.0.1", MOCK_SERVER_PORT).`when`(
                enginePath(engineName).withMethod("GET"),
                Times.exactly(1)
        )
    }


    private fun enginePath(engineName: String? = null): HttpRequest {
        (if (engineName == null) "/api/as/v1/engines" else "/api/as/v1/engines/$engineName").let {
            return HttpRequest.request().withPath(it)
        }

    }

    companion object {
        const val MOCK_SERVER_PORT = 8000
    }


}