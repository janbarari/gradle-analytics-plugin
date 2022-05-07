package io.github.janbarari.gradle.analytics.domain.entity

import org.jetbrains.exposed.sql.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildTest {

    private lateinit var longColumn: Column<Long>
    private lateinit var varcharColumn: Column<String>

    @BeforeAll
    fun setup() {
        longColumn = Column(Table(),"longColumn", LongColumnType())
        varcharColumn = Column(Table(),"varcharColumn", VarCharColumnType())
    }

    @Test
    fun `Check the number column`() {
        //Check the column name is `number`
        assertEquals("number", Build.number.name)
        //Check the column type is `Long`
        assert(Build.number::class.java == longColumn::class.java)
        //Check the column is auto increment
        assert(Build.number.autoIncColumnType?.isAutoInc ?: false)
    }

    @Test
    fun `Check the startedAt column`() {
        //Check the column name is `started_at`
        assertEquals("started_at", Build.startedAt.name)
        //Check the column type is `Long`
        assert(Build.startedAt::class.java == longColumn::class.java)
    }

    @Test
    fun `Check the finishedAt column`() {
        //Check the column name is `finished_at`
        assertEquals("finished_at", Build.finishedAt.name)
        //Check the column type is `Long`
        assert(Build.finishedAt::class.java == longColumn::class.java)
    }

    @Test
    fun `Check the configurationFinishedAt column`() {
        //Check the column name is `configuration_finished_at`
        assertEquals("configuration_finished_at", Build.configurationFinishedAt.name)
        //Check the column type is `Long`
        assert(Build.configurationFinishedAt::class.java == longColumn::class.java)
    }

    @Test
    fun `Check the cmd column`() {
        //Check the column name is `cmd`
        assertEquals("cmd", Build.cmd.name)
        //Check the column type is `Varchar`
        assert(Build.cmd::class.java == varcharColumn::class.java)
    }

    @Test
    fun `Check the os column`() {
        //Check the column name is `os`
        assertEquals("os", Build.os.name)
        //Check the column type is `Varchar`
        assert(Build.os::class.java == varcharColumn::class.java)
    }

    @Test
    fun `Check the table primary-key`() {
        assertEquals("number", Build.primaryKey.columns.first().name)
    }

}