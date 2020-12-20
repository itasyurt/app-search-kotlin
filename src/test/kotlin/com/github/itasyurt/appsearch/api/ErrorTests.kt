package com.github.itasyurt.appsearch.api

import com.github.itasyurt.appsearch.client.api.impl.DefaultHttpClient
import com.github.itasyurt.appsearch.client.api.util.createEndpoint
import com.github.itasyurt.appsearch.client.api.util.startClientAndServer

import com.github.itasyurt.appsearch.client.domain.AppSearchException
import org.junit.Assert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpResponse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ErrorTests {

    class DummyClient(settings: Map<String, Any>) : DefaultHttpClient(settings) {

        fun dummyAction(): String {
            val req = createGetRequest("dummy")
            val resp = httpClient.execute(req)
            return parseResponse(resp) {
                "this should not return"
            }

        }
    }


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
    fun testErrorsParsedCorrectly() {

        createEndpoint("/dummy", method = "GET").callback {
            val bodyStr = """
                {"errors": ["a", "b"]}
                """
            HttpResponse().withBody(bodyStr).withStatusCode(400)
        }

        val dummyClient = DummyClient(mapOf(Pair("baseUrl", "http://localhost:8000"), Pair("apiKey", "dummy")))
        Assert.assertThrows(AppSearchException::class.java) {
            dummyClient.dummyAction()
        }.let {
            Assert.assertEquals(2, it.errors?.size)
        }


    }


    @Test
    fun testMissingErrorsHandledCorrectly() {

        createEndpoint("/dummy", method = "GET").callback {
            val bodyStr = """
                {"message": "This is another error format"}
                """
            HttpResponse().withBody(bodyStr).withStatusCode(400)
        }

        val dummyClient = DummyClient(mapOf(Pair("baseUrl", "http://localhost:8000"), Pair("apiKey", "dummy")))
        Assert.assertThrows(AppSearchException::class.java) {
            dummyClient.dummyAction()
        }.let {
            Assert.assertNull(it.errors)
        }


    }


}