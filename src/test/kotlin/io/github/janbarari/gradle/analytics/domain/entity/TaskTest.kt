package io.github.janbarari.gradle.analytics.domain.entity

import org.jetbrains.exposed.sql.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskTest {

    private lateinit var longColumn: Column<Long>
    private lateinit var varcharColumn: Column<String>

    @BeforeAll
    fun setup() {
        longColumn = Column(Table(),"longColumn", LongColumnType())
        varcharColumn = Column(Table(),"varcharColumn", VarCharColumnType())
    }

}