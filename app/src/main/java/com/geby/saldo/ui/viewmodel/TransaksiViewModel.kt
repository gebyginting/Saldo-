package com.geby.saldo.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.data.pref.UserPreference
import com.geby.saldo.data.repository.TransaksiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransaksiRepository,
    userPreference: UserPreference
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.semuaTransaksi
        .map { list -> list.sortedByDescending { it.date } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saldo hasil kalkulasi
    val saldo: StateFlow<Double> = combine(
        userPreference.saldoAwal,
        repository.totalSaldoFlow
    ) { saldoAwal, saldoDariTransaksi ->
        saldoAwal + saldoDariTransaksi
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalPemasukan: StateFlow<Double> = repository.totalPemasukanFlow
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalPengeluaran: StateFlow<Double> = repository.totalPengeluaranFlow
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun tambahTransaksi(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.tambah(transaction)
                // Tidak perlu update saldo ke DataStore lagi
            } catch (e: Exception) {
                Log.e("TRANSAKSI_ERROR", "Gagal tambah transaksi: ${e.message}", e)
            }
        }
    }


    fun hapusTransaksi(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.hapus(transaction)
            } catch (e: Exception) {
                Log.e("TRANSAKSI_ERROR", "Gagal hapus transaksi: ${e.message}", e)
            }
        }
    }

    fun getTransaksiByJenis(jenis: String) = repository.getByJenis(jenis)
}
