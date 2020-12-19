package org.itasyurt.appsearch.client.api.util.http

import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import java.net.URI


class HttpGetRequestWithBody(val uri: String) : HttpEntityEnclosingRequestBase() {
    override fun getMethod() = "GET"

    init {
        setURI(URI.create(uri))
    }

}

class HttpDeleteRequestWithBody(val uri: String) : HttpEntityEnclosingRequestBase() {
    override fun getMethod() = "DELETE"

    init {
        setURI(URI.create(uri))
    }

}

fun <T : HttpRequest> T.bearer(token: String): T {
    return apply { addHeader("Authorization", "Bearer $token") }
}


fun HttpResponse.isSuccess() = this.statusLine.statusCode in arrayOf(HttpStatus.SC_OK)