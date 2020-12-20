package com.teapp

import com.zaxxer.hikari.HikariDataSource

internal class Config {
    companion object {
        val dataSource: HikariDataSource
            get() {
                val ds = HikariDataSource()
                ds.jdbcUrl = "jdbc:mysql://${System.getenv("DATABASE_URL")}"
                ds.username = System.getenv("DATABASE_USER")
                ds.password = System.getenv("DATABASE_PASSWORD")
                ds.maximumPoolSize = 1
                return ds
            }
    }
}