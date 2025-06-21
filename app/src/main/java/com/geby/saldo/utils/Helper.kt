package com.geby.saldo.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale

object Helper {
    fun formatRupiahTanpaKoma(number: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        return format.format(number)
    }

    fun formatNumberTanpaKoma(amount: Double): String {
        return String.format("%,.0f", amount)
    }

    fun setupCurrencyFormatter(editText: EditText) {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0
            currency = Currency.getInstance("IDR")
        }

        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    editText.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[Rp,.\\s]".toRegex(), "")

                    val parsed = cleanString.toDoubleOrNull()
                    if (parsed != null) {
                        val formatted = format.format(parsed)
                        current = formatted
                        editText.setText(formatted)
                        editText.setSelection(formatted.length)
                    } else {
                        editText.setText("")
                    }

                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    fun cleanSaldo(saldo: String): Double? {
        val cleanSaldo = saldo.replace("[Rp,.\\s]".toRegex(), "")
        val newSaldo = cleanSaldo.toDoubleOrNull()
        return newSaldo
    }

    fun formatTanggalUI(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy - HH.mm", Locale("id", "ID"))
            val date = inputFormat.parse(dateString)

            val calendarNow = Calendar.getInstance()
            val calendarDate = Calendar.getInstance().apply { time = date }

            val isSameDay = calendarNow.get(Calendar.YEAR) == calendarDate.get(Calendar.YEAR)
                    && calendarNow.get(Calendar.DAY_OF_YEAR) == calendarDate.get(Calendar.DAY_OF_YEAR)

            if (isSameDay) {
                val timeFormat = SimpleDateFormat("HH.mm", Locale("id", "ID"))
                "Hari ini - ${timeFormat.format(date!!)}"
            } else {
                outputFormat.format(date!!)
            }
        } catch (e: Exception) {
            "-"
        }
    }



}