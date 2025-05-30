package com.geby.saldo.utils

import java.text.NumberFormat
import java.util.Locale

object Helper {
    fun formatRupiahTanpaKoma(number: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        return format.format(number)
    }

}