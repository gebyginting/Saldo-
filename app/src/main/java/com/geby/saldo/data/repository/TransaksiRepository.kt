package com.geby.saldo.data.repository

import android.content.Context
import com.geby.saldo.data.local.database.AppDatabase
import com.geby.saldo.data.local.database.TransaksiDao
import com.geby.saldo.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransaksiRepository private constructor(context: Context) {

    private val dao: TransaksiDao = AppDatabase.getDatabase(context).transaksiDao()

    val semuaTransaksi = dao.getAllTransaksi()

    val totalSaldoFlow: Flow<Double> = dao.getTotalSaldoFlow()

    val totalPemasukanFlow = dao.getTotalPemasukanFlow()

    val totalPengeluaranFlow = dao.getTotalPengeluaranFlow()

    suspend fun tambah(transaksi: Transaction) = dao.insert(transaksi)

    suspend fun hapus(transaksi: Transaction) = dao.delete(transaksi)

    suspend fun hapusSemua() = dao.deleteAllTransaksi()

    fun getByJenis(jenis: String) = dao.getByJenis(jenis)

    companion object {
        @Volatile
        private var INSTANCE: TransaksiRepository? = null

        fun getInstance(context: Context): TransaksiRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TransaksiRepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}