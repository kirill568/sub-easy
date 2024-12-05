package com.example.subeasy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.subeasy.data.local.entities.Service

@Dao
interface ServiceDao {
    @Insert
    suspend fun insert(service: Service): Long

    @Query("SELECT * FROM service WHERE id = :serviceId")
    suspend fun getServiceById(serviceId: Int): Service

    @Query("SELECT * FROM service")
    fun getAllServices(): LiveData<List<Service>>

    @Query("DELETE FROM service WHERE id = :serviceId")
    suspend fun deleteService(serviceId: Int)
}