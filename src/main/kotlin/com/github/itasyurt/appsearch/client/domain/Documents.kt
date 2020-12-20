package com.github.itasyurt.appsearch.client.domain


typealias  Document = Map<String, Any>
data class DocumentResponse(val results: List<Document?>)
data class ListDocumentResponse(val results: List<Document>, val meta: Map<String, Any>)

data class DeleteDocumentResult(val id: String, val deleted: Boolean)
data class DeleteDocumentResponse(val results: List<DeleteDocumentResult>)

data class SaveDocumentResult(val id: String?, val errors: List<String>)
data class SaveDocumentResponse(val results: List<SaveDocumentResult>)