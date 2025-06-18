package com.geby.saldo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconResId: Int,
    val type: TransactionType,
    val category: TransactionCategory = TransactionCategory.OTHER,
    val isOtherCategory: Boolean = false
)
