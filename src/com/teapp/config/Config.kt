package com.teapp.config

import com.zaxxer.hikari.HikariDataSource

internal class Config {
    companion object {
        val dataSource: HikariDataSource
            get() {
                val dbConfigNotFoundException = Exception("DB config not found in environment variables")
                val databaseHost = System.getenv("DATABASE_HOST") ?: throw dbConfigNotFoundException
                val databasePort = System.getenv("DATABASE_PORT") ?: throw dbConfigNotFoundException
                val databaseName = System.getenv("DATABASE_NAME") ?: throw dbConfigNotFoundException
                val databaseUser = System.getenv("DATABASE_USER") ?: throw dbConfigNotFoundException
                val databaseUserPassword = System.getenv("DATABASE_USER_PASSWORD") ?: throw dbConfigNotFoundException

                val ds = HikariDataSource()
                ds.jdbcUrl = "jdbc:mysql://$databaseHost:$databasePort/$databaseName"
                ds.username = databaseUser
                ds.password = databaseUserPassword
                ds.maximumPoolSize = 1
                return ds
            }
    }
}