package com.example.subeasy.ui.allServices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.subeasy.data.local.AppDatabase
import com.example.subeasy.data.local.entities.Service

class AllServicesViewModel(application: Application) : AndroidViewModel(application) {
    private val serviceDao = AppDatabase.getInstance(application).serviceDao()
    val allServices: LiveData<List<Service>> = serviceDao.getAllServices()

    private val _filteredServices = MutableLiveData<List<Service>>()
    val filteredServices: LiveData<List<Service>> get() = _filteredServices

    // Фильтрация списка сервисов
    fun filterServices(query: String) {
        _filteredServices.value = allServices.value?.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }
}