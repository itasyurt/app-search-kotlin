package org.itasyurt.appsearch.client.api

import org.itasyurt.appsearch.client.domain.FieldType
import org.itasyurt.appsearch.client.domain.Schema
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
class SchemaTests {

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
    fun testGetSchema() {

        val client = createClient()
        val engineName = "engineName"

        mockSchemaEndpoint(engineName, method = "GET").callback {
            val resultsBody =
                    """
                {
                    "name":"text",
                    "rating": "number",
                    "last_update_date": "date",
                    "location": "geolocation"
                }
                """
            HttpResponse.response(resultsBody)

        }

        val schema = client.schemas.get(engineName)
        Assert.assertEquals(4, schema.fields.size)
        Assert.assertEquals(FieldType.TEXT, schema.fields["name"])
        Assert.assertEquals(FieldType.NUMBER, schema.fields["rating"])
        Assert.assertEquals(FieldType.DATE, schema.fields["last_update_date"])
        Assert.assertEquals(FieldType.GEOLOCATION, schema.fields["location"])


    }

    @Test
    fun testUpdateSchema() {

        val client = createClient()
        val engineName = "engineName"

        val expectedBody = """
                {
                    "name":"text",
                    "location": "geolocation"
                }
                """

        mockSchemaEndpoint(engineName, method = "POST", expectedBody = expectedBody).callback {
            val resultsBody =
                    """
                {
                    "name":"text",
                    "rating": "number",
                    "last_update_date": "date",
                    "location": "geolocation"
                }
                """
            HttpResponse.response(resultsBody)

        }


        val updateSchema = Schema(fields = mapOf(Pair("name", FieldType.TEXT), Pair("location", FieldType.GEOLOCATION)))
        val schema = client.schemas.update(engineName, updateSchema)

        Assert.assertEquals(4, schema.fields.size)
        Assert.assertEquals(FieldType.TEXT, schema.fields["name"])
        Assert.assertEquals(FieldType.NUMBER, schema.fields["rating"])
        Assert.assertEquals(FieldType.DATE, schema.fields["last_update_date"])
        Assert.assertEquals(FieldType.GEOLOCATION, schema.fields["location"])


    }

    private fun mockSchemaEndpoint(engineName: String, method: String = "GET", expectedBody: String? = null): ForwardChainExpectation {
        var path = "/api/as/v1/engines/$engineName/schema"

        return createEndpoint(path, method, expectedBody = expectedBody)
    }


}

