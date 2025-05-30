package com.geby.saldo.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.geby.saldo.data.pref.UserPreference
import com.geby.saldo.data.repository.TransaksiRepository
import com.geby.saldo.di.Injection

class ViewModelFactory private constructor(
    private val userPreference: UserPreference,
    private val transaksiRepository: TransaksiRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(userPreference) as T
            }
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                TransactionViewModel(transaksiRepository, userPreference) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val userPref = UserPreference.getInstance(context)
                instance ?: ViewModelFactory(userPref, Injection.provideTransaksiRepository(context))
            }.also { instance = it }
    }
}
