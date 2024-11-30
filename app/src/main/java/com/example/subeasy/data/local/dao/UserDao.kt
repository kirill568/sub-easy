package com.example.subeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.subeasy.data.local.entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: Int): User

    @Query("SELECT * FROM user ORDER BY id DESC LIMIT 1")
    fun getActiveUser(): User?

    @Query("DELETE FROM user WHERE id = :userId")
    suspend fun deleteUser(userId: Int)

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)
}
