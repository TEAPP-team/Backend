package com.teapp

import com.zaxxer.hikari.*

internal class Config {
    companion object {
        val dataSource: HikariDataSource
            get() {
                var ds = HikariDataSource()
                ds.jdbcUrl = "jdbc:mysql://us-cdbr-east-02.cleardb.com:3306/heroku_9732ad107b64a9e"
                ds.username = "b2cab30efb683c"
                ds.password = "63e90cc3"
                return ds
            }
    }
}