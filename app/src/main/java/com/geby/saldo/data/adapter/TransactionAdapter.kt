package com.geby.saldo.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.geby.saldo.R
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.data.model.TransactionType
import com.geby.saldo.databinding.ItemTransaksiBinding

class TransactionAdapter(
    val transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

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
        val item = transactions[position]
        with(holder.binding) {
            txtTitle.text = item.title
            txtDate.text = item.date
            imgCategory.setImageResource(item.categoryIconRes)

            val formattedAmount = "Rp %,d".format(item.amount.toInt())
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

    override fun getItemCount(): Int = transactions.size
}
