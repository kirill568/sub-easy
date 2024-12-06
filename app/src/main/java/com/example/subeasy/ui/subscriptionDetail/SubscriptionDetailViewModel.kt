package com.example.subeasy.ui.subscriptionDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.subeasy.data.local.AppDatabase
import com.example.subeasy.data.local.entities.Subscription
import com.example.subeasy.data.local.entities.SubscriptionWithService
import kotlinx.coroutines.launch

class SubscriptionDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val subscriptionDao = AppDatabase.getInstance(application).subscriptionDao()

    private val _subscriptionDetail = MutableLiveData<SubscriptionWithService>()
    val subscriptionDetail: LiveData<SubscriptionWithService> get() = _subscriptionDetail

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadSubscription(subscriptionId: Int) {
        _loading.value = true

        viewModelScope.launch {
            val detail = subscriptionDao.getSubscriptionWithService(subscriptionId)
            _subscriptionDetail.postValue(detail)

            _loading.postValue(false)
        }
    }

    fun deleteSubscription(subscription: Subscription, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                subscriptionDao.deleteSubscription(subscription.id)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
