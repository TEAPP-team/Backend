package com.teapp.service

import com.teapp.models.User

class UserService {
    fun getUserById(userId: Int, db: DatabaseFactory): User = db.getUserById(userId)
}