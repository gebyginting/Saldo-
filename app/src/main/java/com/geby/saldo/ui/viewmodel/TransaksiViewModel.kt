package com.geby.saldo.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geby.saldo.data.model.SortOption
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.data.model.TransactionCategory
import com.geby.saldo.data.pref.UserPreference
import com.geby.saldo.data.repository.TransaksiRepository
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val _kategoriFilter = MutableStateFlow<TransactionCategory?>(null)
    private val kategoriFilter: StateFlow<TransactionCategory?> = _kategoriFilter

    private val _sortOption = MutableStateFlow(SortOption.TERBARU)
    val sortOption: StateFlow<SortOption> = _sortOption

    //    filter berdasarkan kategori
    val transactions: StateFlow<List<Transaction>> = repository.semuaTransaksi
        .map { it -> it.sortedByDescending { it.date } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        transactions,
        _kategoriFilter,
        _sortOption
    ) { transactions, kategori, sortOption ->
        var filtered = if (kategori == null) {
            transactions
        } else {
            transactions.filter { it.category == kategori }
        }

        filtered = when (sortOption) {
            SortOption.TERBARU -> filtered.sortedByDescending { it.date }
            SortOption.TERLAMA -> filtered.sortedBy { it.date }
            SortOption.NOMINAL_TERTINGGI -> filtered.sortedByDescending { it.amount }
            SortOption.NOMINAL_TERENDAH -> filtered.sortedBy { it.amount }
        }

        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun setKategoriFilter(kategoriStr: String) {
        // Kalau pilih "Semua" berarti null (tidak pakai filter)
        if (kategoriStr.equals("Semua", ignoreCase = true)) {
            _kategoriFilter.value = null
        } else {
            // Cari enum yang cocok dengan nama kategori (case-insensitive)
            _kategoriFilter.value = TransactionCategory.entries.find {
                it.name.equals(kategoriStr.replace(" ", "").uppercase(), ignoreCase = true)
            }
        }
    }

    //    filter berdasarkan nominal
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

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

    fun hapusSemuaTransaksi() {
        viewModelScope.launch {
            try {
                repository.hapusSemua()
            } catch (e: Exception) {
                Log.e("TRANSAKSI_ERROR", "Gagal hapus semua transaksi: ${e.message}", e)
            }
        }
    }

}
