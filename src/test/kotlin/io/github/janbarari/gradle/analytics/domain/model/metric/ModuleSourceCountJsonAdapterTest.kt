package io.github.janbarari.gradle.analytics.domain.model.metric

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import io.github.janbarari.gradle.extension.isNotNull
import okio.Buffer
import org.gradle.internal.impldep.com.google.gson.JsonParser
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModuleSourceCountJsonAdapterTest {

    lateinit var moshi: Moshi
    lateinit var adapter: ModuleSourceCountJsonAdapter

    @BeforeAll
    fun setup() {
        moshi = Moshi.Builder().build()
        adapter = ModuleSourceCountJsonAdapter(moshi)
    }

    @Test
    fun `Check fromJson() returns valid data model with valid json`() {
        val json = """
            {
                "path": ":app",
                "value": 100
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
            fromReader.path == ":app"
        }
        assertTrue {
            fromReader.value == 100
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
                    Buffer().writeUtf8(json)
                )
            )
        }
        assertThrows<JsonDataException> {
            val json = """
                {
                    "path": null
                }
            """.trimIndent()
            adapter.fromJson(
                JsonReader.of(
                    Buffer().writeUtf8(json)
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
        val validModel = ModuleSourceCount(
            path = ":app",
            value = 100
        )
        assertDoesNotThrow {
            JsonParser.parseString(adapter.toJson(validModel))
        }
    }

}