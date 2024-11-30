package com.example.subeasy.ui.welcome

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subeasy.data.local.AppDatabase
import com.example.subeasy.data.local.entities.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getInstance(application).userDao()

    private val _isUserCreated = MutableLiveData<Boolean>()
    val isUserCreated: LiveData<Boolean> get() = _isUserCreated

    fun addUser(firstName: String, lastName: String) {
        viewModelScope.launch {
            try {
                val newUser = User(
                    firstName = firstName,
                    lastName = lastName
                )
                userDao.insert(newUser)

                _isUserCreated.postValue(true)
            } catch (e: Exception) {
                _isUserCreated.postValue(false)
                throw e
            }
        }
    }
}