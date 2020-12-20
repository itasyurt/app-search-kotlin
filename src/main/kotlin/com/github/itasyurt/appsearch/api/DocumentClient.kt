package com.github.itasyurt.appsearch.api

import com.github.itasyurt.appsearch.client.domain.*


interface DocumentClient {

    fun create(engineName: String, vararg documents: Document): SaveDocumentResponse
    fun delete(engineName: String, vararg docIds: String): DeleteDocumentResponse
    fun patch(engineName: String, vararg documents: Document): SaveDocumentResponse
    fun get(engineName: String, vararg docIds: String): DocumentResponse
    fun list(engineName: String, pagination: Pagination = Pagination(current = 1, size = 100)): ListDocumentResponse

}