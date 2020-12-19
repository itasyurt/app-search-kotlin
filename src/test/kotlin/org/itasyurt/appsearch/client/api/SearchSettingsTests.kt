package org.itasyurt.appsearch.client.api

import org.itasyurt.appsearch.client.domain.*
import org.itasyurt.appsearch.client.domain.Function
import org.itasyurt.appsearch.client.api.util.createClient
import org.itasyurt.appsearch.client.api.util.createEndpoint
import org.itasyurt.appsearch.client.api.util.startClientAndServer
import org.junit.Assert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.client.server.ForwardChainExpectation
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpResponse


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchSettingsTests {

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
    fun testResetSettings() {

        val client = createClient()
        val engineName = "engineName"

        mockSettingsResetEndpoint(engineName).callback {
            val resultsBody =
                    """
                {
                    "search_fields": 
                        {
                            "headline": {"weight" :1 },
                            "title": {"weight": 2.3}
                        },
                    "result_fields": {},
                    "boosts": {}
                }
                """
            HttpResponse.response(resultsBody)

        }

        val searchSettings = client.searchSettings.reset(engineName)

        Assert.assertEquals(2, searchSettings.searchFields.size)
        Assert.assertEquals(2.3, searchSettings.searchFields["title"]!!.weight)
        Assert.assertEquals(1, searchSettings.searchFields["headline"]!!.weight)


    }


    @Test
    fun testGetSearchFields() {

        val client = createClient()
        val engineName = "engineName"

        mockSettingsEndpoint(engineName, method = "GET").callback {
            val resultsBody =
                    """
                {
                    "search_fields": 
                        {
                            "headline": {"weight" :1 },
                            "title": {"weight": 2.3}
                        },
                    "result_fields": {},
                    "boosts": {}
                }
                """
            HttpResponse.response(resultsBody)

        }

        val searchSettings = client.searchSettings.get(engineName)

        Assert.assertEquals(2, searchSettings.searchFields.size)
        Assert.assertEquals(2.3, searchSettings.searchFields["title"]!!.weight)
        Assert.assertEquals(1, searchSettings.searchFields["headline"]!!.weight)

    }


    @Test
    fun testResultFields() {

        val client = createClient()
        val engineName = "engineName"

        mockSettingsEndpoint(engineName, method = "GET").callback {
            val resultsBody =
                    """
                {
                    "search_fields": {},
                    "result_fields": 
                        {
                            "title": {"raw": {"size": 30}, "snippet": {}},
                            "name": { "snippet": {"fallback": true, "size": 120}},
                            "headline": { "raw": {}}
                        },
                    "boosts" :{}
                }
                """
            HttpResponse.response(resultsBody)

        }

        val searchSettings = client.searchSettings.get(engineName)

        Assert.assertEquals(3, searchSettings.resultFields.size)
        Assert.assertNull(searchSettings.resultFields["headline"]!!.raw!!.size)
        Assert.assertEquals(30, searchSettings.resultFields["title"]!!.raw!!.size)
        Assert.assertNull(searchSettings.resultFields["name"]!!.raw)
        Assert.assertNull(searchSettings.resultFields["headline"]!!.snippet)
        Assert.assertNull(searchSettings.resultFields["title"]!!.snippet!!.fallback)
        Assert.assertNull(searchSettings.resultFields["title"]!!.snippet!!.size)
        Assert.assertEquals(true, searchSettings.resultFields["name"]!!.snippet!!.fallback)
        Assert.assertEquals(120, searchSettings.resultFields["name"]!!.snippet!!.size)


    }


    @Test
    fun testBoosts() {

        val client = createClient()
        val engineName = "engineName"

        mockSettingsEndpoint(engineName, method = "GET").callback {
            val resultsBody =
                    """
                {
                    "search_fields": {},
                    "result_fields": {},
                    "boosts" :{
                            "name": [   
                                        {
                                            "type": "value",
                                            "value": true,
                                            "operation": "multiply",
                                            "factor": 10
                                        },
                                        {
                                            "type": "value",
                                            "value": ["java", "python"],
                                            "operation": "multiply"
                                        },
                                        {
                                            "type": "functional",
                                            "function": "linear"
                                        },
                                        {
                                            "type": "proximity",
                                            "center": 1,
                                            "function": "linear"
                                        }
                            ],
                            "title": {"type":"functional", "function": "exponential"}
                    }

                }
                """
            HttpResponse.response(resultsBody)

        }

        val searchSettings = client.searchSettings.get(engineName)

        Assert.assertEquals(2, searchSettings.boosts.size)
        Assert.assertEquals(4, searchSettings.boosts["name"]!!.size)
        Assert.assertEquals(true, (searchSettings.boosts["name"]!![0] as ValueBoost).value[0])
        Assert.assertEquals(10, (searchSettings.boosts["name"]!![0] as ValueBoost).factor)
        Assert.assertEquals(Operation.MULTIPLY, (searchSettings.boosts["name"]!![0] as ValueBoost).operation)
        Assert.assertEquals(listOf("java", "python"), (searchSettings.boosts["name"]!![1] as ValueBoost).value)
        Assert.assertNull((searchSettings.boosts["name"]!![1] as ValueBoost).factor)
        Assert.assertEquals(Function.LINEAR, (searchSettings.boosts["name"]!![2] as FunctionalBoost).function)
        Assert.assertEquals(ProximityFunction.LINEAR, (searchSettings.boosts["name"]!![3] as ProximityBoost).function)
        Assert.assertEquals(1, (searchSettings.boosts["name"]!![3] as ProximityBoost).center)
        Assert.assertEquals(Function.EXPONENTIAL, (searchSettings.boosts["title"]!![0] as FunctionalBoost).function)

    }

    @Test
    fun testUpdateSettings() {

        val client = createClient()
        val engineName = "engineName"

        val expectedBody =
                """
                {
                    "search_fields": {
                            "headline": {"weight" :1 }
                    },
                    "result_fields": {
                            "headline": {"raw": {"size": 30}, "snippet": {}}
                    },
                    "boosts" :{
                            "name": [
                                        {
                                            "type": "value",
                                            "value": ["java", "python"],
                                            "operation": "multiply"
                                        }
                                       
                            ]
                    }

                }
                """

        mockSettingsEndpoint(engineName, method = "PUT", expectedBody = expectedBody).callback {
            HttpResponse.response(expectedBody)
        }

        val searchFields = mapOf(Pair("headline", SearchWeight(weight = 1)))
        val resultFields = mapOf(Pair("headline", ResultField(raw = Raw(30), snippet = Snippet())))
        val boosts = mapOf(Pair("name", listOf(ValueBoost(value = listOf("java", "python"), operation = Operation.MULTIPLY))))
        val searchSettings = SearchSettings(searchFields = searchFields, resultFields = resultFields, boosts = boosts)

        val updatedSettings = client.searchSettings.update(engineName, searchSettings)

        Assert.assertEquals(1, updatedSettings.boosts.size)

    }


    private fun mockSettingsEndpoint(engineName: String, method: String = "GET", expectedBody: String? = null): ForwardChainExpectation {
        var path = "/api/as/v1/engines/$engineName/search_settings"


        return createEndpoint(path, method, expectedBody = expectedBody)
    }


    private fun mockSettingsResetEndpoint(engineName: String): ForwardChainExpectation {
        var path = "/api/as/v1/engines/$engineName/search_settings/reset"

        return createEndpoint(path, "POST")
    }


}

