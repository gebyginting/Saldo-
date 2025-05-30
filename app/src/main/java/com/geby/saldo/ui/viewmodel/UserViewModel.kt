package com.geby.saldo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.geby.saldo.data.pref.UserPreference
import kotlinx.coroutines.launch

class UserViewModel(private val userPreference: UserPreference) : ViewModel() {

    val name: LiveData<String> = userPreference.name.asLiveData()
//    val saldo: LiveData<Double> = userPreference.saldoA.asLiveData()
    val darkMode: LiveData<Boolean> = userPreference.darkMode.asLiveData()

    suspend fun saveUser(name: String, saldo: Double) {
        userPreference.saveUser(name, saldo)
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreference.setDarkMode(enabled)
        }
    }
}
