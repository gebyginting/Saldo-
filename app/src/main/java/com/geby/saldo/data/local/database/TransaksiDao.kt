package com.geby.saldo.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geby.saldo.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaksiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaksi: Transaction)

    @Delete
    suspend fun delete(transaksi: Transaction)

    @Query("SELECT * FROM `transaction` ORDER BY date DESC")
    fun getAllTransaksi(): Flow<List<Transaction>>


    @Query("SELECT * FROM `transaction` WHERE type = :jenis ORDER BY date DESC")
    fun getByJenis(jenis: String): LiveData<List<Transaction>>

    @Query("SELECT SUM(amount) FROM `transaction` WHERE type = 'INCOME'")
    fun getTotalPemasukanFlow(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM `transaction` WHERE type = 'EXPENSE'")
    fun getTotalPengeluaranFlow(): Flow<Double?>

    @Query("""
    SELECT 
        IFNULL((SELECT SUM(amount) FROM `transaction` WHERE type = 'INCOME'), 0) - 
        IFNULL((SELECT SUM(amount) FROM `transaction` WHERE type = 'EXPENSE'), 0)
""")
    fun getTotalSaldoFlow(): Flow<Double>

}