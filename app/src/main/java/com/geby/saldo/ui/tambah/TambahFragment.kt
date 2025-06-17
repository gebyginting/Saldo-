package com.geby.saldo.ui.tambah

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.geby.saldo.R
import com.geby.saldo.data.model.Category
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.data.model.TransactionType
import com.geby.saldo.databinding.FragmentTambahBinding
import com.geby.saldo.ui.viewmodel.KategoriViewModel
import com.geby.saldo.ui.viewmodel.TransactionViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TambahFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTambahBinding? = null
    private val binding get() = _binding!!

    private val factory by lazy { ViewModelFactory.getInstance(requireContext()) }
    private val transaksiViewModel: TransactionViewModel by viewModels { factory }
    private val categoryViewModel: KategoriViewModel by viewModels { factory }

    private var isPengeluaran = true
    private var allCategoryList: List<Category> = emptyList()
    private var selectedCategory: Category? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToggleButtons()
        observeKategori()
        setupListeners()
    }

    private fun observeKategori() {
        categoryViewModel.kategoriList.observe(viewLifecycleOwner) { list ->
            allCategoryList = list
            Log.d("TambahFragment", "Kategori Diterima: ${list.map { it.name }}")
            updateKategoriDropdown()
        }
        categoryViewModel.loadKategori()
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener { handleSave() }
    }

    private fun handleSave() {
        val nominalText = binding.etJumlah.text.toString()
        val catatan = binding.etNama.text.toString()

        if (selectedCategory == null) {
            toast("Pilih kategori terlebih dahulu")
            return
        }

        if (catatan.isBlank()) {
            binding.etJumlah.error = "Nama transaksi tidak boleh kosong"
            return
        }

        if (nominalText.isBlank()) {
            binding.etJumlah.error = "Nominal tidak boleh kosong"
            return
        }

        val nominal = nominalText.toDoubleOrNull()
        if (nominal == null || nominal <= 0) {
            binding.etJumlah.error = "Nominal tidak valid"
            return
        }

        val transaksi = Transaction(
            title = catatan,
            date = getCurrentDateString(),
            amount = nominal,
            type = currentTransactionType(),
            category = selectedCategory!!.category,
            categoryIconRes = selectedCategory!!.iconResId
        )

        lifecycleScope.launch {
            transaksiViewModel.tambahTransaksi(transaksi)
            toast("Transaksi berhasil disimpan")
            dismiss()
        }
    }

    private fun setupToggleButtons() = with(binding) {
        val btnPengeluaran = btnPengeluaran
        val btnPemasukan = btnPemasukan
        val ivTransaksi = ivTransaksi

        fun setActive(
            selected: MaterialButton,
            other: MaterialButton,
            activeColor: Int,
            iconRes: Int
        ) {
            selected.apply {
                setBackgroundColor(getColor(activeColor))
                setTextColor(getColor(R.color.white))
                strokeWidth = 4
                strokeColor = ColorStateList.valueOf(getColor(activeColor))
            }
            other.apply {
                setBackgroundColor(getColor(R.color.lightGrey))
                setTextColor(getColor(R.color.black))
                strokeWidth = 0
            }

            ivTransaksi.setImageResource(iconRes)
        }

        btnPengeluaran.setOnClickListener {
            isPengeluaran = true
            setActive(btnPengeluaran, btnPemasukan, R.color.red, R.drawable.ic_expense)
            updateKategoriDropdown()
        }

        btnPemasukan.setOnClickListener {
            isPengeluaran = false
            setActive(btnPemasukan, btnPengeluaran, R.color.green, R.drawable.ic_income2)
            updateKategoriDropdown()
        }

        // Set default
        btnPengeluaran.performClick()
    }

    private fun updateKategoriDropdown() {
        val categories = getCategoryByType(currentTransactionType())
        val items = categories.map { it.name }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )

        binding.actKategori.setAdapter(adapter)
        binding.actKategori.setText("") // reset pilihan

        // show dropdown ketika diklik
        binding.actKategori.setOnClickListener {
            binding.actKategori.showDropDown()
        }

        // simpan kategori yang dipilih
        binding.actKategori.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = categories[position]
            Log.d("TambahFragment", "Kategori Dipilih: ${selectedCategory?.name}")
        }
    }


    private fun getCategoryByType(type: TransactionType) = allCategoryList.filter { it.type == type }

    private fun currentTransactionType(): TransactionType = if (isPengeluaran) TransactionType.EXPENSE else TransactionType.INCOME

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }

    private fun getColor(resId: Int): Int = ContextCompat.getColor(requireContext(), resId)

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}