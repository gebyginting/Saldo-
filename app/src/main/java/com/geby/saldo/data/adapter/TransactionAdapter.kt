package com.geby.saldo.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geby.saldo.R
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.data.model.TransactionType
import com.geby.saldo.databinding.ItemTransaksiBinding
import com.geby.saldo.utils.Helper.formatTanggalUI

class TransactionAdapter(
    private var currencySymbol: String = "Rp"
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    inner class TransactionViewHolder(val binding: ItemTransaksiBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransaksiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            txtTitle.text = item.title
            txtDate.text = formatTanggalUI(item.date)
            imgCategory.setImageResource(item.categoryIconRes)

            val formattedAmount = "$currencySymbol%,d".format(item.amount.toInt())
            txtAmount.text = if (item.type == TransactionType.INCOME) {
                "+ $formattedAmount"
            } else {
                "- $formattedAmount"
            }

            val context = txtAmount.context
            txtAmount.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (item.type == TransactionType.INCOME) R.color.green else R.color.red
                )
            )
        }
    }

    fun updateCurrencySymbol(symbol: String) {
        currencySymbol = symbol
        notifyDataSetChanged()
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            // Misal pakai ID untuk cek kesamaan
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            // Cek seluruh isi sama atau tidak
            return oldItem == newItem
        }
    }
}
