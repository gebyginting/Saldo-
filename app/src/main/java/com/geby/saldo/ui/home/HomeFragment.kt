package com.geby.saldo.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geby.saldo.data.adapter.TransactionAdapter
import com.geby.saldo.data.model.TransactionType
import com.geby.saldo.databinding.FragmentHomeBinding
import com.geby.saldo.ui.tambah.TambahFragment
import com.geby.saldo.ui.viewmodel.TransactionViewModel
import com.geby.saldo.ui.viewmodel.UserViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory
import com.geby.saldo.utils.Helper
import com.geby.saldo.utils.PieChartManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionAdapter: TransactionAdapter
    private val factory: ViewModelFactory by lazy { ViewModelFactory.getInstance(requireContext()) }
    private val userViewModel: UserViewModel by  viewModels { factory }
    private val viewModel: TransactionViewModel by viewModels { factory }
    private var currentSymbol: String = "Rp"

    companion object {
        var USER_NAME = "USER_NAME"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserPreference()
        setupRecyclerView()
        setupFab()
        setupSwipeToDelete()
    }

    private fun setupUserPreference() {
        userViewModel.name.observe(viewLifecycleOwner) { name ->
            Log.d("USERNAME:", name)
            USER_NAME = name
            binding.textWelcome.text = "Halo, $name \uD83D\uDC4B"
        }

        // Observe simbol mata uang
        userViewModel.currencySymbol.observe(viewLifecycleOwner) { symbol ->
            currentSymbol = symbol
            transactionAdapter.updateCurrencySymbol(symbol)
            updateSaldoAndStatic()
        }

        // Collect saldo dan pemasukan/pengeluaran
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.saldo.collect {
                        updateSaldoText(it)
                    }
                }
                launch {
                    viewModel.totalPemasukan.collect {
                        updatePemasukanText(it)
                    }
                }
                launch {
                    viewModel.totalPengeluaran.collect {
                        updatePengeluaranText(it)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(currentSymbol)
        binding.recyclerViewRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRiwayat.adapter = transactionAdapter

        setupRecentTransaction()
    }

    private fun setupRecentTransaction() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transactions.collect { transactionList ->
                    if (transactionList.isEmpty()) {
                        binding.emptyTransactionMsg.visibility = View.VISIBLE
                        transactionAdapter.submitList(emptyList())
                        PieChartManager.setupEmptyChart(binding.pieChart)
                        return@collect
                    }

                    binding.emptyTransactionMsg.visibility = View.GONE

                    val recentList = transactionList.sortedByDescending { it.date }.take(3)
                    transactionAdapter.submitList(recentList)

                    val income = transactionList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val expense = transactionList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                    PieChartManager.setupChart(binding.pieChart, income.toFloat(), expense.toFloat())
                }
            }
        }
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transaction = transactionAdapter.currentList[position]
                viewModel.hapusTransaksi(transaction)  // Asumsi ada fungsi deleteTransaction di ViewModel
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewRiwayat)
    }

    private fun setupFab() {
        binding.fabTambahTransaksi.setOnClickListener {
            val bottomSheet = TambahFragment()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    // UPDATE SALDO
    private fun updateSaldoAndStatic() {
        updateSaldoText(viewModel.saldo.value)
        updatePemasukanText(viewModel.totalPemasukan.value)
        updatePengeluaranText(viewModel.totalPengeluaran.value)
    }

    private fun updateSaldoText(saldo: Double) {
        binding.textSaldo.text = "$currentSymbol${Helper.formatNumberTanpaKoma(saldo)}"
    }

    private fun updatePemasukanText(pemasukan: Double) {
        binding.tvTotalIncome.text = "$currentSymbol${Helper.formatNumberTanpaKoma(pemasukan)}"
    }

    private fun updatePengeluaranText(pengeluaran: Double) {
        binding.tvTotalExpense.text = "$currentSymbol${Helper.formatNumberTanpaKoma(pengeluaran)}"
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
