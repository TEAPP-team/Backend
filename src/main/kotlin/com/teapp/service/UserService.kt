package com.teapp.service

import com.teapp.models.Comment
import com.teapp.models.Post
import com.teapp.models.User

class UserService {
    fun getUserById(userId: Int, db: DatabaseFactory): User = db.getUserById(userId)

    fun addComment(comment: Comment, db: DatabaseFactory) = db.addComment(comment)
}