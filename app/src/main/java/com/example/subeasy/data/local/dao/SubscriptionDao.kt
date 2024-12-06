package com.example.subeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.subeasy.data.local.entities.Subscription
import com.example.subeasy.data.local.entities.SubscriptionWithService

@Dao
interface SubscriptionDao {
    @Transaction
    @Query("SELECT * FROM subscription WHERE serviceId = :serviceId")
    suspend fun getSubscriptionsByService(serviceId: Int): List<Subscription>

    @Query("SELECT * FROM subscription")
    fun getAllSubscriptions(): List<Subscription>

    @Query("SELECT * FROM subscription")
    @Transaction
    suspend fun getAllSubscriptionsWithServices(): List<SubscriptionWithService>

    @Transaction
    @Query("SELECT * FROM subscription WHERE id = :subscriptionId")
    suspend fun getSubscriptionWithService(subscriptionId: Int): SubscriptionWithService

    @Update
    suspend fun updateSubscription(subscription: Subscription)

    @Insert
    suspend fun insert(subscription: Subscription)

    @Query("DELETE FROM subscription WHERE id = :subscriptionId")
    suspend fun deleteSubscription(subscriptionId: Int)
}