package io.github.janbarari.gradle.analytics.data.database.table

import org.jetbrains.exposed.sql.autoIncColumnType
import org.jetbrains.exposed.sql.isAutoInc
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MetricTableTest {

    @Test
    fun `check number column`() {
        assertEquals(true, MetricTable.number.autoIncColumnType?.isAutoInc)
        assertEquals("number", MetricTable.number.name)
        assertEquals(false, MetricTable.number.columnType.nullable)
    }

    @Test
    fun `check createdAt column`() {
        assertEquals(false, MetricTable.createdAt.autoIncColumnType?.isAutoInc ?: false)
        assertEquals("created_at", MetricTable.createdAt.name)
        assertEquals(false, MetricTable.createdAt.columnType.nullable)
    }

    @Test
    fun `check branch column`() {
        assertEquals(false, MetricTable.branch.autoIncColumnType?.isAutoInc ?: false)
        assertEquals("branch", MetricTable.branch.name)
        assertEquals(false, MetricTable.branch.columnType.nullable)
    }

    @Test
    fun `check requestedTasks column`() {
        assertEquals(false, MetricTable.requestedTasks.autoIncColumnType?.isAutoInc ?: false)
        assertEquals("requested_tasks", MetricTable.requestedTasks.name)
        assertEquals(false, MetricTable.requestedTasks.columnType.nullable)
    }

    @Test
    fun `check value column`() {
        assertEquals(false, MetricTable.value.autoIncColumnType?.isAutoInc ?: false)
        assertEquals("value", MetricTable.value.name)
        assertEquals(false, MetricTable.value.columnType.nullable)
    }

}