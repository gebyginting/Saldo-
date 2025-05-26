package com.geby.saldo.ui.transaksi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.geby.saldo.R
import com.geby.saldo.data.adapter.TransactionAdapter
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.data.model.TransactionType
import com.geby.saldo.databinding.FragmentTransaksiBinding

class TransaksiFragment : Fragment() {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.fabTambahTransaksi) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom + 16) // kasih offset biar agak naik
            insets
        }
        setupRecyclerView()
        loadSampleTransactions()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList())
        binding.rvTransaksi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransaksi.adapter = transactionAdapter
    }

    private fun loadSampleTransactions() {
        val sampleList = listOf(
            Transaction(1, "Gaji Bulanan", "01 Mei 2025", 5000000.0, TransactionType.INCOME, R.drawable.ic_income),
            Transaction(2, "Makan Siang", "02 Mei 2025", 25000.0, TransactionType.EXPENSE, R.drawable.ic_food),
            Transaction(3, "Transport", "02 Mei 2025", 10000.0, TransactionType.EXPENSE, R.drawable.ic_transport),
            Transaction(1, "Gaji Bulanan", "01 Mei 2025", 5000000.0, TransactionType.INCOME, R.drawable.ic_income),
            Transaction(2, "Makan Siang", "02 Mei 2025", 25000.0, TransactionType.EXPENSE, R.drawable.ic_food),
            Transaction(3, "Transport", "02 Mei 2025", 10000.0, TransactionType.EXPENSE, R.drawable.ic_transport),
            Transaction(1, "Gaji Bulanan", "01 Mei 2025", 5000000.0, TransactionType.INCOME, R.drawable.ic_income),
            Transaction(2, "Makan Siang", "02 Mei 2025", 25000.0, TransactionType.EXPENSE, R.drawable.ic_food),
            Transaction(3, "Transport", "02 Mei 2025", 10000.0, TransactionType.EXPENSE, R.drawable.ic_transport),
            Transaction(1, "Gaji Bulanan", "01 Mei 2025", 5000000.0, TransactionType.INCOME, R.drawable.ic_income),
            Transaction(2, "Makan Siang", "02 Mei 2025", 25000.0, TransactionType.EXPENSE, R.drawable.ic_food),
            Transaction(3, "Transport", "02 Mei 2025", 10000.0, TransactionType.EXPENSE, R.drawable.ic_transport),
            )
        transactionAdapter = TransactionAdapter(sampleList)
        binding.rvTransaksi.adapter = transactionAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}