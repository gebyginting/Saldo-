package com.geby.saldo.ui.tambah

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.geby.saldo.R
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.data.model.TransactionCategory
import com.geby.saldo.data.model.TransactionType
import com.geby.saldo.databinding.FragmentTambahBinding
import com.geby.saldo.ui.viewmodel.TransactionViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class TambahFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTambahBinding? = null
    private val binding get() = _binding!!
    private val factory: ViewModelFactory by lazy { ViewModelFactory.getInstance(requireContext()) }
    private val transaksiViewModel: TransactionViewModel by viewModels { factory }

    private var isPemasukan = false

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
        setupKategoriDropdown()

        binding.btnSave.setOnClickListener {
            val nominalText = binding.etJumlah.text.toString()
            val catatan = binding.etNama.text.toString()
            val jenis = binding.spinnerJenis.selectedItem?.toString() ?: ""

            if (jenis == "🔽 Pilih jenis..." || jenis.isBlank()) {
                Toast.makeText(requireContext(), "Pilih jenis kategori", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nominalText.isBlank()) {
                binding.etJumlah.error = "Nominal tidak boleh kosong"
                return@setOnClickListener
            }

            val nominal = nominalText.toDoubleOrNull()
            if (nominal == null || nominal <= 0) {
                binding.etJumlah.error = "Nominal tidak valid"
                return@setOnClickListener
            }

            val type = if (isPemasukan) TransactionType.INCOME else TransactionType.EXPENSE
            val iconRes = if (isPemasukan)
                kategoriPemasukan[jenis] ?: R.drawable.ic_income2
            else
                kategoriPengeluaran[jenis] ?: R.drawable.ic_expense

            val date = getCurrentDateString()

            // Convert jenis (String) ke TransactionCategory enum, case-insensitive dan hapus spasi
            val categoryEnum = TransactionCategory.entries.find {
                it.name.equals(jenis.replace(" ", "").uppercase(), ignoreCase = true)
            } ?: TransactionCategory.MAKANAN // fallback jika tidak ketemu, sesuaikan

            val transaksi = Transaction(
                title = catatan,
                date = date,
                amount = nominal,
                type = type,
                category = categoryEnum,
                categoryIconRes = iconRes
            )

            lifecycleScope.launch {
                transaksiViewModel.tambahTransaksi(transaksi)
                dismiss()
                Toast.makeText(requireContext(), "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getCurrentDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }

    private fun setupToggleButtons() {
        val btnPengeluaran = binding.btnPengeluaran
        val btnPemasukan = binding.btnPemasukan
        val ivTransaksi = binding.ivTransaksi

        fun setActive(
            selected: MaterialButton,
            other: MaterialButton,
            activeColor: Int,
            iconRes: Int
        ) {
            selected.setBackgroundColor(ContextCompat.getColor(requireContext(), activeColor))
            selected.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            selected.strokeWidth = 4
            selected.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), activeColor))

            other.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightGrey))
            other.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            other.strokeWidth = 0

            ivTransaksi.setImageResource(iconRes)
        }

        btnPengeluaran.setOnClickListener {
            isPemasukan = false
            setActive(btnPengeluaran, btnPemasukan, R.color.red, R.drawable.ic_expense)
            setupKategoriDropdown()
        }

        btnPemasukan.setOnClickListener {
            isPemasukan = true
            setActive(btnPemasukan, btnPengeluaran, R.color.green, R.drawable.ic_income2)
            setupKategoriDropdown()
        }

        // Default aktif: pengeluaran
        btnPengeluaran.performClick()
    }

    private fun setupKategoriDropdown() {
        val spinner = binding.spinnerJenis
        val kategoriList = if (isPemasukan) {
            listOf("🔽 Pilih jenis...") + kategoriPemasukan.keys
        } else {
            listOf("🔽 Pilih jenis...") + kategoriPengeluaran.keys
        }

        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            kategoriList
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
    }

    private val kategoriPengeluaran = mapOf(
        "Makanan" to R.drawable.ic_food,
        "Transportasi" to R.drawable.ic_transport,
        "Pendidikan" to R.drawable.ic_education,
        "Lainnya" to R.drawable.ic_expense
    )

    private val kategoriPemasukan = mapOf(
        "Gaji" to R.drawable.ic_income,
        "Lainnya" to R.drawable.ic_income2
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
