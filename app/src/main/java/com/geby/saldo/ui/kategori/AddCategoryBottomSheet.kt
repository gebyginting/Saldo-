package com.geby.saldo.ui.kategori

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geby.saldo.R
import com.geby.saldo.data.adapter.TemplateCategoryAdapter
import com.geby.saldo.data.local.item.DummyItems.dummyOptionCategoryItems
import com.geby.saldo.data.model.Category
import com.geby.saldo.data.model.TransactionType
import com.geby.saldo.databinding.FragmentAddCategoryBottomSheetBinding
import com.geby.saldo.ui.viewmodel.KategoriViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddCategoryBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddCategoryBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: KategoriViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext().applicationContext)
    }

    private lateinit var adapter: TemplateCategoryAdapter
    private var selectedCategory: Category? = null
    private var currentType: TransactionType = TransactionType.EXPENSE

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCategoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToggle()
        setupRecyclerView()
        setupSaveButton()

        // Default pilihan pertama
        binding.toggleGroupType.check(R.id.btnExpense)
        filterTemplates(TransactionType.EXPENSE)
    }


    private fun setupToggle() {
        binding.toggleGroupType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            currentType = when (checkedId) {
                R.id.btnIncome -> TransactionType.INCOME
                R.id.btnExpense -> TransactionType.EXPENSE
                else -> return@addOnButtonCheckedListener
            }

            filterTemplates(currentType)
        }
    }

    private fun setupRecyclerView() {
        adapter = TemplateCategoryAdapter(emptyList()) { selected ->
            selectedCategory = selected
            adapter.setSelectedCategory(selected)
        }

        binding.rvKategoriTemplate.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)
            adapter = this@AddCategoryBottomSheetFragment.adapter
        }
    }

    private fun filterTemplates(type: TransactionType) {
        val filtered = dummyOptionCategoryItems.filter { it.type == type }
        selectedCategory = null
        adapter.submitList(filtered)
    }

    private fun setupSaveButton() {
        binding.btnSimpan.setOnClickListener {
            if (selectedCategory != null) {
                // Cek apakah kategori sudah ada
                if (viewModel.categoryExisted(selectedCategory)) {
                    Toast.makeText(
                        requireContext(),
                        "Kategori '${selectedCategory?.name}' sudah ada.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                viewModel.tambahKategori(selectedCategory!!)
                dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Choose_category_first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
