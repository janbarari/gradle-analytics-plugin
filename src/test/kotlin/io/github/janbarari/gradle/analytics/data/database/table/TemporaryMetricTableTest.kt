package io.github.janbarari.gradle.analytics.data.database.table

import org.jetbrains.exposed.sql.autoIncColumnType
import org.jetbrains.exposed.sql.isAutoInc
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TemporaryMetricTableTest {

    @Test
    fun `check number column`() {
        assertEquals(true, TemporaryMetricTable.number.autoIncColumnType?.isAutoInc)
        assertEquals("number", TemporaryMetricTable.number.name)
        assertEquals(false, TemporaryMetricTable.number.columnType.nullable)
    }

    @Test
    fun `check createdAt column`() {
        assertEquals(false, TemporaryMetricTable.createdAt.autoIncColumnType?.isAutoInc ?: false)
        assertEquals("created_at", TemporaryMetricTable.createdAt.name)
        assertEquals(false, TemporaryMetricTable.createdAt.columnType.nullable)
    }

    @Test
    fun `check branch column`() {
        assertEquals(false, TemporaryMetricTable.branch.autoIncColumnType?.isAutoInc ?: false)
        assertEquals("branch", TemporaryMetricTable.branch.name)
        assertEquals(false, TemporaryMetricTable.branch.columnType.nullable)
    }

    @Test
    fun `check requestedTasks column`() {
        assertEquals(false, TemporaryMetricTable.requestedTasks.autoIncColumnType?.isAutoInc ?: false)
        assertEquals("requested_tasks", TemporaryMetricTable.requestedTasks.name)
        assertEquals(false, TemporaryMetricTable.requestedTasks.columnType.nullable)
    }

    @Test
    fun `check value column`() {
        assertEquals(false, TemporaryMetricTable.value.autoIncColumnType?.isAutoInc ?: false)
        assertEquals("value", TemporaryMetricTable.value.name)
        assertEquals(false, TemporaryMetricTable.value.columnType.nullable)
    }

}