package io.github.janbarari.gradle.analytics.domain.model.report

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import io.github.janbarari.gradle.extension.isNotNull
import org.gradle.internal.impldep.com.google.gson.JsonParser
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CacheHitReportJsonAdapterTest {

    lateinit var moshi: Moshi
    lateinit var adapter: CacheHitReportJsonAdapter

    @BeforeAll
    fun setup() {
        moshi = Moshi.Builder().build()
        adapter = CacheHitReportJsonAdapter(moshi)
    }

    @Test
    fun `Check fromJson() returns valid data model with valid json`() {
        val json = """
            {
                "modules": [
                    {
                        "path": ":core",
                        "rate": 20,
                        "diffRate": 32,
                        "meanValues": [
                            {
                                "value": 12,
                                "description": "11/08/2022"
                            }
                        ]
                    }
                ],
                "overallDiffRate": 33,
                "overallRate": 3,
                "overallMeanValues": [
                    {
                        "value": 30,
                        "description": "22/10/2022"
                    }
                ]
            }
        """.trimIndent()

        val fromReader = adapter.fromJson(
            JsonReader.of(
                okio.Buffer().writeUtf8(json)
            )
        )
        assertTrue {
            fromReader.isNotNull()
        }
        assertTrue {
            fromReader.overallMeanValues.isNotEmpty()
        }
        assertTrue {
            fromReader.modules.isNotEmpty()
        }
        assertTrue {
            fromReader.overallRate == 3L
        }
        assertTrue {
            fromReader.overallDiffRate == 33F
        }
    }

    @Test
    fun `Check fromJson() throws exception with unValid json`() {
        assertThrows<JsonEncodingException> {
            val json = """
            {
                Is is fake json
            }
        """.trimIndent()
            adapter.fromJson(
                JsonReader.of(
                    okio.Buffer().writeUtf8(json)
                )
            )
        }
        assertThrows<JsonDataException> {
            val json = """
                {
                    "meanValues": null
                }
            """.trimIndent()
            adapter.fromJson(
                JsonReader.of(
                    okio.Buffer().writeUtf8(json)
                )
            )
        }
    }

    @Test
    fun `Check toString() is not empty`() {
        assertTrue { adapter.toString().isNotEmpty() }
    }

    @Test
    fun `Check toJson() throws NullPointerException with unValid data model`() {
        val unValidModel = null
        assertThrows<NullPointerException> {
            adapter.toJson(unValidModel)
        }
    }

    @Test
    fun `Check toJson() return valid Json with valid data model`() {
        val validModel = CacheHitReport(
            modules = emptyList(),
            overallDiffRate = 44F,
            overallRate = 33L,
            overallMeanValues = emptyList()
        )
        assertDoesNotThrow {
            JsonParser.parseString(adapter.toJson(validModel))
        }
    }

}