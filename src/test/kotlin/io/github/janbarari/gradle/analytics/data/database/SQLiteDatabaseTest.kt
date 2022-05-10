package io.github.janbarari.gradle.analytics.data.database

import org.junit.jupiter.api.Test

class SQLiteDatabaseTest {

    @Test
    fun `check the database creates successfully with isQueryLogEnabled=true`() {
        val dc = DatabaseConfig()
        dc.isQueryLogEnabled = true
        dc.url = "build/temporary_lt.db"
        dc.user = "root"
        SQLiteDatabase.connect(dc)
        assert(true)
    }

    @Test
    fun `check the database creates successfully with isQueryLogEnabled=false`() {
        val dc = DatabaseConfig()
        dc.isQueryLogEnabled = false
        dc.url = "build/temporary_lf.db"
        dc.user = "root"
        SQLiteDatabase.connect(dc)
        assert(true)
    }

}