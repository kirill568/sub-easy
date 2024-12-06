package com.example.subeasy.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subeasy.data.local.AppDatabase
import com.example.subeasy.data.local.entities.Subscription
import com.example.subeasy.data.local.entities.SubscriptionWithService
import com.example.subeasy.data.local.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getInstance(application).userDao()

    private val subscriptionDao = AppDatabase.getInstance(application).subscriptionDao()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _subscriptionsWithServices = MutableLiveData<List<SubscriptionWithService>>()
    val subscriptionsWithServices: LiveData<List<SubscriptionWithService>> = _subscriptionsWithServices

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _emptyState = MutableLiveData<Boolean>()
    val emptyState: LiveData<Boolean> = _emptyState

    private val _totalCostForCurrentMonth = MutableLiveData<Double>()
    val totalCostForCurrentMonth: LiveData<Double> = _totalCostForCurrentMonth

    fun fetchData() {
        _loading.value = true
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) { userDao.getActiveUser() }
            var allSubscriptionsWithService = withContext(Dispatchers.IO) { subscriptionDao.getAllSubscriptionsWithServices() }
            allSubscriptionsWithService = allSubscriptionsWithService.sortedBy { it.subscription.calculateNextPaymentDate() }

            _user.postValue(user)
            _subscriptionsWithServices.postValue(allSubscriptionsWithService)

            if (allSubscriptionsWithService.isNotEmpty()) {
                _totalCostForCurrentMonth.postValue(calculateTotalCostForCurrentMonth(allSubscriptionsWithService.map { it.subscription }))
                _emptyState.postValue(false)
            } else {
                _emptyState.postValue(true)
            }
            _loading.postValue(false)
        }
    }

    private fun calculateTotalCostForCurrentMonth(subscriptions: List<Subscription>): Double {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        return subscriptions
            .filter { subscription ->
                val subscriptionCalendar = Calendar.getInstance().apply {
                    timeInMillis = subscription.startedOn
                }
                val subscriptionYear = subscriptionCalendar.get(Calendar.YEAR)
                val subscriptionMonth = subscriptionCalendar.get(Calendar.MONTH)

                val monthsSinceStart = (currentYear - subscriptionYear) * 12 + (currentMonth - subscriptionMonth)

                monthsSinceStart >= 0 && monthsSinceStart % subscription.cycle.months == 0
            }
            .sumOf { it.cost.toBigDecimal() }
            .setScale(2, RoundingMode.HALF_EVEN)
            .toDouble()
    }

}