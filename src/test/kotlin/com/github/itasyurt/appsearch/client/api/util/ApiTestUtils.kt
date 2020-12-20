package com.github.itasyurt.appsearch.client.api.util

import com.github.itasyurt.appsearch.client.DefaultClient
import org.mockserver.client.server.ForwardChainExpectation
import org.mockserver.client.server.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.MatchType
import org.mockserver.matchers.Times
import org.mockserver.model.HttpRequest
import org.mockserver.model.JsonBody

private const val  MOCK_SERVER_PORT =8000

fun startClientAndServer(): ClientAndServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)

fun createClient() = DefaultClient(url = "http://localhost:$MOCK_SERVER_PORT", apiKey = "someKey")

fun createEndpoint(path: String, method: String, expectedBody:String?=null): ForwardChainExpectation {
    val request = HttpRequest.request().withPath(path)

    if(expectedBody!=null) {
        request.withBody(JsonBody.json(expectedBody, MatchType.STRICT)).withHeader("Content-Type", "application/json; charset=UTF-8")

    }
    return MockServerClient("127.0.0.1", MOCK_SERVER_PORT).`when`(
            request.withMethod(method),
            Times.exactly(1))
}

