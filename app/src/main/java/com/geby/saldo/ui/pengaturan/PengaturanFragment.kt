package com.geby.saldo.ui.pengaturan

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.geby.saldo.R
import com.geby.saldo.data.model.Transaction
import com.geby.saldo.data.model.TransactionCategory
import com.geby.saldo.data.model.TransactionType
import com.geby.saldo.databinding.FragmentPengaturanBinding
import com.geby.saldo.ui.viewmodel.TransactionViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")
class PengaturanFragment : Fragment() {

    private var _binding: FragmentPengaturanBinding? = null
    private val binding get() = _binding!!
    private val factory: ViewModelFactory by lazy { ViewModelFactory.getInstance(requireContext()) }
    private val viewModel: TransactionViewModel by activityViewModels { factory }

    companion object {
        private const val PICK_CSV_FILE = 1001

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPengaturanBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cvExportData.setOnClickListener { exportData() }
        binding.cvImportData.setOnClickListener { openFilePicker() }
        binding.btnDeleteData.setOnClickListener { deleteData() }
    }

    private fun deleteData() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Semua Transaksi")
            .setMessage("Apakah kamu yakin ingin menghapus semua data transaksi?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.hapusSemuaTransaksi()
            }
            .setNegativeButton("Batal", null)
            .show()

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

    // IMPORT CSV FILE
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Pilih File CSV"), PICK_CSV_FILE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CSV_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val fileName = getFileName(uri)
                if (fileName?.endsWith(".csv", ignoreCase = true) == true) {
                    importCsvFromUri(uri)
                } else {
                    Toast.makeText(requireContext(), "File bukan CSV", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        val returnCursor = requireContext().contentResolver.query(uri, null, null, null, null)
        returnCursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            return it.getString(nameIndex)
        }
        return null
    }

    private fun importCsvFromUri(uri: Uri) {
        lifecycleScope.launch {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val csvLines = inputStream?.bufferedReader()?.readLines() ?: return@launch

                val transactions = csvLines.drop(1) // skip header
                    .mapNotNull { line ->
                        val parts = line.split(",")
                        if (parts.size >= 5) {
                            try {
                                val category = TransactionCategory.valueOf(parts[1])
                                Transaction(
                                    title = parts[2],
                                    date = parts[0],
                                    amount = parts[3].toDoubleOrNull() ?: 0.0,
                                    type = TransactionType.valueOf(parts[4].uppercase()),
                                    category = category,
                                    categoryIconRes = getIconResForCategory(category)
                                )
                            } catch (e: Exception) {
                                null // skip invalid lines
                            }
                        } else null
                    }

                transactions.forEach {
                    viewModel.tambahTransaksi(it)
                }

                Toast.makeText(requireContext(), "Import berhasil: ${transactions.size} transaksi", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal mengimpor data", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    // get icon based on category
    private fun getIconResForCategory(category: TransactionCategory): Int {
        return when (category) {
            TransactionCategory.OTHER_EXPENSE -> R.drawable.ic_other_expense
            TransactionCategory.OTHER_INCOME -> R.drawable.ic_other_income
            TransactionCategory.MAKANAN -> R.drawable.ic_food
            TransactionCategory.TRANSPORTASI -> R.drawable.ic_food
            TransactionCategory.PENDIDIKAN -> R.drawable.ic_education
            TransactionCategory.KESEHATAN -> R.drawable.ic_medical
            TransactionCategory.DEBIT -> R.drawable.ic_credit_card
            TransactionCategory.UTILITIES -> R.drawable.ic_electrical_services
            TransactionCategory.HIBURAN -> R.drawable.ic_entertainment
            TransactionCategory.KERJA -> R.drawable.ic_work
            TransactionCategory.HADIAH -> R.drawable.ic_gift
            TransactionCategory.UANG_SAKU -> R.drawable.ic_allowance
            TransactionCategory.PET_CARE -> R.drawable.ic_pet_care
            TransactionCategory.BODY_CARE -> R.drawable.ic_body_care
            TransactionCategory.DONATION -> R.drawable.ic_donation
            TransactionCategory.FREELANCE -> R.drawable.ic_freelance
            TransactionCategory.HOUSING -> R.drawable.ic_housing
            TransactionCategory.INVESTMENT -> R.drawable.ic_investment
            TransactionCategory.LAUNDRY -> R.drawable.ic_laundry
            TransactionCategory.LOTTERY -> R.drawable.ic_lottery
            TransactionCategory.PARKING -> R.drawable.ic_parking
            TransactionCategory.ROYALTY -> R.drawable.ic_royalties
            TransactionCategory.SCHOLARSHIP ->R.drawable.ic_scholarship
            TransactionCategory.VOCATION -> R.drawable.ic_vocation
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}