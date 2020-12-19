package com.teapp

import com.zaxxer.hikari.HikariDataSource

internal class Config {
    companion object {
        val dataSource: HikariDataSource
            get() {
                val ds = HikariDataSource()
                ds.jdbcUrl = "jdbc:mysql://eu-cdbr-west-03.cleardb.net:3306/heroku_a903ae47a367a9b"
                ds.username = "bce9eb6aad7af2"
                ds.password = "817f4224"
                ds.maximumPoolSize = 1
                return ds
            }
    }
}