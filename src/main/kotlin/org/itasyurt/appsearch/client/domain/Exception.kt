package org.itasyurt.appsearch.client.domain

class AppSearchException(val statusCode: Int, val body: String, val errors:List<String>?) : Exception()