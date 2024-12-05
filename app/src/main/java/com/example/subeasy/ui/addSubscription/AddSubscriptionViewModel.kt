package com.example.subeasy.ui.addSubscription

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.subeasy.data.local.AppDatabase
import com.example.subeasy.data.local.entities.Service
import com.example.subeasy.data.local.entities.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddSubscriptionViewModel(application: Application) : AndroidViewModel(application) {

    private val serviceDao = AppDatabase.getInstance(application).serviceDao()
    private val subscriptionDao = AppDatabase.getInstance(application).subscriptionDao()

    private val _isSubscriptionCreated = MutableLiveData<Boolean>()
    val isSubscriptionCreated: LiveData<Boolean> get() = _isSubscriptionCreated

    fun getServiceById(serviceId: Int, onResult: (Service?) -> Unit) {
        viewModelScope.launch {
            val service = serviceDao.getServiceById(serviceId)
            onResult(service)
        }
    }

    fun addServiceAndSubscription(serviceName: String, subscription: Subscription) {
        viewModelScope.launch {
            try {
                val service = Service(iconPath = "unknown.png", name = serviceName)
                val serviceId = serviceDao.insert(service).toInt()
                subscription.serviceId = serviceId
                subscriptionDao.insert(subscription)

                _isSubscriptionCreated.postValue(true)
            } catch (e: Exception) {
                Log.e("AddServiceSubscription", "Error adding service and subscription", e)
                _isSubscriptionCreated.postValue(false)
            }
        }
    }

    fun addSubscription(subscription: Subscription) {
        viewModelScope.launch {
            try {
                subscriptionDao.insert(subscription)

                _isSubscriptionCreated.postValue(true)
            } catch (e: Exception) {
                Log.e("AddServiceSubscription", "Error adding service and subscription", e)
                _isSubscriptionCreated.postValue(false)
            }
        }
    }
}
