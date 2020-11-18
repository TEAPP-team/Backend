package com.teapp

import com.zaxxer.hikari.HikariDataSource

internal class Config {
    companion object {
        val dataSource: HikariDataSource
            get() {
                val ds = HikariDataSource()
                ds.jdbcUrl = "jdbc:mysql://"
                ds.username = ""
                ds.password = ""
                ds.maximumPoolSize = 1
                return ds
            }
    }
}