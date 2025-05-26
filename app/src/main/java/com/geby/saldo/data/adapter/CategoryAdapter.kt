package com.geby.saldo.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geby.saldo.data.model.Category
import com.geby.saldo.databinding.ItemKategoriBinding

class CategoryAdapter(
    private val categories: List<Category>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemKategoriBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemKategoriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categories[position]
        holder.binding.txtNamaKategori.text = item.name
        holder.binding.imgIconKategori.setImageResource(item.iconResId)
    }

    override fun getItemCount(): Int = categories.size
}
