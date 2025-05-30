package com.geby.saldo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.geby.saldo.utils.TransactionTypeConverter

@Entity
@TypeConverters(TransactionTypeConverter::class)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String, // kamu bisa simpan sebagai String (yyyy-MM-dd) atau Long (timestamp)
    val amount: Double,
    val type: TransactionType,
    val categoryIconRes: Int
)

enum class TransactionType {
    INCOME, EXPENSE
}