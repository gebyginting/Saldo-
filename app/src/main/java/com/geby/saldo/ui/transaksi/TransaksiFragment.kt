package com.geby.saldo.ui.transaksi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.geby.saldo.R
import com.geby.saldo.data.adapter.TransactionAdapter
import com.geby.saldo.data.model.SortOption
import com.geby.saldo.databinding.FragmentTransaksiBinding
import com.geby.saldo.ui.viewmodel.TransactionViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory
import com.geby.saldo.utils.Helper
import kotlinx.coroutines.launch

class TransaksiFragment : Fragment() {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionAdapter: TransactionAdapter
    private val factory: ViewModelFactory by lazy { ViewModelFactory.getInstance(requireContext()) }
    private val transaksiViewModel: TransactionViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rvTransaksi) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupFilterSpinner()
        setupSortSpinner()
        observeTransactions()
    }

    private fun setupSortSpinner() {
        val sortOptions = resources.getStringArray(R.array.sort_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSort.adapter = adapter

        binding.spinnerSort.setSelection(0)
        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = when (position) {
                    0 -> SortOption.TERBARU
                    1 -> SortOption.TERLAMA
                    2 -> SortOption.NOMINAL_TERTINGGI
                    3 -> SortOption.NOMINAL_TERENDAH
                    else -> SortOption.TERBARU
                }
                transaksiViewModel.setSortOption(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.rvTransaksi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransaksi.adapter = transactionAdapter
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transaksiViewModel.filteredTransactions.collect { list ->
                    transactionAdapter.submitList(list)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    transaksiViewModel.totalPemasukan.collect {
                        binding.tvTotalPemasukan.text = Helper.formatRupiahTanpaKoma(it)
                    }
                }
                launch {
                    transaksiViewModel.totalPengeluaran.collect {
                        binding.tvTotalPengeluaran.text = Helper.formatRupiahTanpaKoma(it)
                    }
                }
            }
        }
    }

    private fun setupFilterSpinner() {
        val kategoriArray = resources.getStringArray(R.array.kategori_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kategoriArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerKategori.adapter = adapter

        binding.spinnerKategori.setSelection(0)
        binding.spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedKategori = kategoriArray[position]
                transaksiViewModel.setKategoriFilter(selectedKategori)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tidak perlu aksi khusus
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}