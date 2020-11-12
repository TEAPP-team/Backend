package com.teapp.service

import com.teapp.Config
import org.jetbrains.exposed.sql.Database

fun getTeahouse(id: Int){
    Database.connect(Config.dataSource)

}

class DatabaseFactory