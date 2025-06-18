package com.geby.saldo.ui.pengaturan

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.databinding.FragmentPengaturanBinding
import com.geby.saldo.ui.viewmodel.TransactionViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class PengaturanFragment : Fragment() {

    private var _binding: FragmentPengaturanBinding? = null
    private val binding get() = _binding!!
    private val factory: ViewModelFactory by lazy { ViewModelFactory.getInstance(requireContext()) }
    private val viewModel: TransactionViewModel by activityViewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPengaturanBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cvExportData.setOnClickListener { exportData() }
    }

    private fun exportData() {
        lifecycleScope.launch {
            val transactions = viewModel.transactions
                .filter { it.isNotEmpty() } // hanya jika ada data
                .first()

            val success = saveToCsvFile(transactions)

            if (success) {
                Toast.makeText(requireContext(), "Data berhasil diekspor", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Gagal mengekspor data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToCsvFile(transactions: List<Transaction>): Boolean {
        return try {
            val csv = buildCsvString(transactions)
            val fileName = "export-transaksi-${System.currentTimeMillis()}.csv"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            file.writeText(csv)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun buildCsvString(transactions: List<Transaction>): String = buildString {
        append("Tanggal,Kategori,Transaksi,Nominal,Type\n")
        transactions.forEach { tx ->
            append("${tx.date},${tx.category},${tx.title},${tx.amount},${tx.type}\n")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}