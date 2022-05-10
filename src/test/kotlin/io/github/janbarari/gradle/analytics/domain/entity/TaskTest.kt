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

    @Test
    fun `check the id column`() {
        //Check the column name is `id`
        assertEquals("id", Task.id.name)
        //Check the column type is `Long`
        assert(Task.id::class.java == longColumn::class.java)
        //Check the column is auto increment
        assert(Task.id.autoIncColumnType?.isAutoInc ?: false)
    }

    @Test
    fun `check the name column`() {
        //Check the column name is `name`
        assertEquals("name", Task.name.name)
        //Check the column type is `Varchar`
        assert(Task.name::class.java == varcharColumn::class.java)
    }

    @Test
    fun `check the path column`() {
        //Check the column name is `path`
        assertEquals("path", Task.path.name)
        //Check the column type is `Varchar`
        assert(Task.path::class.java == varcharColumn::class.java)
    }

    @Test
    fun `check the module column`() {
        //Check the column name is `module`
        assertEquals("module", Task.module.name)
        //Check the column type is `Varchar`
        assert(Task.module::class.java == varcharColumn::class.java)
    }

    @Test
    fun `check the startedAt column`() {
        //Check the column name is `started_at`
        assertEquals("started_at", Task.startedAt.name)
        //Check the column type is `Long`
        assert(Task.startedAt::class.java == longColumn::class.java)
    }

    @Test
    fun `check the finishedAt column`() {
        //Check the column name is `finished_at`
        assertEquals("finished_at", Task.finishedAt.name)
        //Check the column type is `Long`
        assert(Task.finishedAt::class.java == longColumn::class.java)
    }

    @Test
    fun `check the buildNumber column`() {
        //Check the column name is `build_number`
        assertEquals("build_number", Task.buildNumber.name)
        //Check the column type is `Long`
        assert(Task.buildNumber::class.java == longColumn::class.java)
        //Check the column references
        val referencedTable = Task.buildNumber.referee?.table?.tableName
        assertEquals("build", referencedTable)
        val referencedColumn = Task.buildNumber.referee?.name
        assertEquals("number", referencedColumn)
    }

    @Test
    fun `check the table primary-key`() {
        assertEquals("id", Task.primaryKey.columns.first().name)
    }

}