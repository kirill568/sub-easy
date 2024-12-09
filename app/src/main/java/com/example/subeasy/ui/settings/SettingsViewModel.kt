package com.example.subeasy.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.subeasy.data.local.AppDatabase
import com.example.subeasy.data.local.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getInstance(application).userDao()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()

    val isUpdateEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        fun hasChanges(): Boolean {
            val currentUser = _user.value
            return currentUser?.let {
                it.firstName != firstName.value?.trim() ||
                        it.lastName != lastName.value?.trim()
            } ?: false
        }

        addSource(_user) { value = hasChanges() }
        addSource(firstName) { value = hasChanges() }
        addSource(lastName) { value = hasChanges() }
    }

    fun loadUser() {
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) { userDao.getActiveUser() }

            _user.postValue(user)
        }
    }

    fun saveChanges(onComplete: () -> Unit) {
        _user.value?.let { currentUser ->
            val updatedUser = currentUser.copy(
                firstName = firstName.value?.trim() ?: currentUser.firstName,
                lastName = lastName.value?.trim() ?: currentUser.lastName
            )
            viewModelScope.launch {
                userDao.updateUser(updatedUser)
                onComplete()
            }
        }
    }
}
