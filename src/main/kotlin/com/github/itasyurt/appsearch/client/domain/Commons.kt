package com.github.itasyurt.appsearch.client.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule


data class Pagination(val current: Int = 1, val size: Int = 20)

val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())
