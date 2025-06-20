package com.geby.saldo.utils

import android.content.Context
import android.net.Uri
import com.geby.saldo.R
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.data.model.TransactionCategory
import com.geby.saldo.data.model.TransactionType
import java.io.File

object TransactionCsvHelper {

    fun exportToCsv(transactions: List<Transaction>, file: File): Boolean {
        return try {
            file.writeText(buildCsvString(transactions))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun importFromCsv(context: Context, uri: Uri): List<Transaction> {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("File tidak bisa dibuka")

        val csvLines = inputStream.bufferedReader().readLines()
        return csvLines.drop(1) // skip header
            .mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size >= 5) {
                    try {
                        val category = TransactionCategory.valueOf(parts[1])
                        Transaction(
                            title = parts[2],
                            date = parts[0],
                            amount = parts[3].toDoubleOrNull() ?: 0.0,
                            type = TransactionType.valueOf(parts[4].uppercase()),
                            category = category,
                            categoryIconRes = getIconForCategory(category)
                        )
                    } catch (e: Exception) {
                        null
                    }
                } else null
            }
    }

    private fun buildCsvString(transactions: List<Transaction>): String = buildString {
        append("Tanggal,Kategori,Transaksi,Nominal,Type\n")
        transactions.forEach {
            append("${it.date},${it.category},${it.title},${it.amount},${it.type}\n")
        }
    }

    private fun getIconForCategory(category: TransactionCategory): Int {
        return when (category) {
            TransactionCategory.OTHER_EXPENSE -> R.drawable.ic_other_expense
            TransactionCategory.OTHER_INCOME -> R.drawable.ic_other_income
            TransactionCategory.MAKANAN -> R.drawable.ic_food
            TransactionCategory.TRANSPORTASI -> R.drawable.ic_food
            TransactionCategory.PENDIDIKAN -> R.drawable.ic_education
            TransactionCategory.KESEHATAN -> R.drawable.ic_medical
            TransactionCategory.DEBIT -> R.drawable.ic_credit_card
            TransactionCategory.UTILITIES -> R.drawable.ic_electrical_services
            TransactionCategory.HIBURAN -> R.drawable.ic_entertainment
            TransactionCategory.KERJA -> R.drawable.ic_work
            TransactionCategory.HADIAH -> R.drawable.ic_gift
            TransactionCategory.UANG_SAKU -> R.drawable.ic_allowance
            TransactionCategory.PET_CARE -> R.drawable.ic_pet_care
            TransactionCategory.BODY_CARE -> R.drawable.ic_body_care
            TransactionCategory.DONATION -> R.drawable.ic_donation
            TransactionCategory.FREELANCE -> R.drawable.ic_freelance
            TransactionCategory.HOUSING -> R.drawable.ic_housing
            TransactionCategory.INVESTMENT -> R.drawable.ic_investment
            TransactionCategory.LAUNDRY -> R.drawable.ic_laundry
            TransactionCategory.LOTTERY -> R.drawable.ic_lottery
            TransactionCategory.PARKING -> R.drawable.ic_parking
            TransactionCategory.ROYALTY -> R.drawable.ic_royalties
            TransactionCategory.SCHOLARSHIP -> R.drawable.ic_scholarship
            TransactionCategory.VOCATION -> R.drawable.ic_vocation
        }
    }
}
