package com.example.subeasy.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class SubscriptionWithService (
    @Embedded val subscription: Subscription,
    @Relation(
        parentColumn = "serviceId",
        entityColumn = "id"
    )
    val service: Service
)