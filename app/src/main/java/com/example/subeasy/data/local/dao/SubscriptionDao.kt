package com.example.subeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.subeasy.data.local.entities.Subscription

@Dao
interface SubscriptionDao {
    @Insert
    suspend fun insert(subscription: Subscription)

    @Query("SELECT * FROM subscription WHERE userId = :userId")
    suspend fun getSubscriptionsByUser(userId: Int): List<Subscription>

    @Query("SELECT * FROM subscription WHERE serviceId = :serviceId")
    suspend fun getSubscriptionsByService(serviceId: Int): List<Subscription>

    @Query("DELETE FROM subscription WHERE id = :subscriptionId")
    suspend fun deleteSubscription(subscriptionId: Int)

    @Update
    suspend fun updateSubscription(subscription: Subscription)
}