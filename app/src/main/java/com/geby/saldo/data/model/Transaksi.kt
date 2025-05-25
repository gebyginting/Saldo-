package com.geby.saldo.data.model

data class Transaction(
    val id: Int,
    val title: String,
    val date: String,
    val amount: Double,
    val type: TransactionType,
    val categoryIconRes: Int
)

enum class TransactionType {
    INCOME, EXPENSE
}