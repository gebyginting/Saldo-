package com.geby.saldo.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geby.saldo.R
import com.geby.saldo.data.model.Category
import com.geby.saldo.databinding.ItemTemplateCategoryBinding

class TemplateCategoryAdapter(
    private var items: List<Category>,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<TemplateCategoryAdapter.ViewHolder>() {

    private var selectedCategory: Category? = null

    inner class ViewHolder(private val binding: ItemTemplateCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.tvName.text = category.name
            binding.ivIcon.setImageResource(category.iconResId)

            if (category == selectedCategory) {
                binding.root.setBackgroundResource(R.drawable.bg_selected_border)
            } else {
                binding.root.background = null
            }

            binding.root.setOnClickListener {
                val previousSelected = selectedCategory
                selectedCategory = category
                notifyItemChanged(items.indexOf(previousSelected))
                notifyItemChanged(adapterPosition)

                onClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTemplateCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setSelectedCategory(category: Category?) {
        val previousSelected = selectedCategory
        selectedCategory = category
        notifyItemChanged(items.indexOf(previousSelected))
        notifyItemChanged(items.indexOf(selectedCategory))
    }

    fun getSelectedCategory(): Category? = selectedCategory

    fun submitList(newList: List<Category>) {
        items = newList
        selectedCategory = null
        notifyDataSetChanged()
    }
}