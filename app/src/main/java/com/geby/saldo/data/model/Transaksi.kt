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
    val category: TransactionCategory,
    val categoryIconRes: Int
)

enum class TransactionCategory {
    MAKANAN,
    TRANSPORTASI,
    PENDIDIKAN,
    KESEHATAN,
    DEBIT,
    UTILITIES,
    HIBURAN,
    KERJA,
    HADIAH,
    UANG_SAKU,
    PET_CARE,
    BODY_CARE,
    DONATION,
    FREELANCE,
    HOUSING,
    INVESTMENT,
    LAUNDRY,
    LOTTERY,
    PARKING,
    ROYALTY,
    SCHOLARSHIP,
    VOCATION,
    OTHER;

    companion object {
        fun fromString(value: String): TransactionCategory? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}

enum class TransactionType {
    INCOME, EXPENSE
}

enum class SortOption {
    TERBARU,
    TERLAMA,
    NOMINAL_TERTINGGI,
    NOMINAL_TERENDAH;

    companion object {
        fun fromString(value: String): SortOption? {
            return SortOption.entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}

