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
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.geby.saldo.data.pref.UserPreference
import com.geby.saldo.databinding.FragmentPengaturanBinding
import com.geby.saldo.ui.viewmodel.TransactionViewModel
import com.geby.saldo.ui.viewmodel.UserViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory
import com.geby.saldo.utils.TransactionCsvHelper
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")
class PengaturanFragment : Fragment() {

    private var _binding: FragmentPengaturanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val userViewModel: UserViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var userPreference: UserPreference

    companion object {
        private const val PICK_CSV_FILE = 1001
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPengaturanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreference = UserPreference.getInstance(requireContext())
        setupUI()
        setupActions()
        themeSetting()
    }

    private fun setupUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userPreference.currencySymbol.collect { symbol ->
                    binding.tvSelectedCurrency.text = symbol
                }
            }
        }
    }

    private fun themeSetting() {
        val switchDarkMode = binding.switchDarkMode

        userViewModel.darkMode.observe(viewLifecycleOwner) { isDarkMode ->
            switchDarkMode.isChecked = isDarkMode

            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            userViewModel.toggleDarkMode(isChecked)
        }
    }

    private fun setupActions() {
        binding.cvExportData.setOnClickListener { exportDataToCsv() }
        binding.cvImportData.setOnClickListener { openFilePicker() }
        binding.btnDeleteData.setOnClickListener { confirmDeleteAllData() }
        binding.cvCurrency.setOnClickListener { showCurrencySelectionDialog() }
    }

    private fun confirmDeleteAllData() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Semua Transaksi")
            .setMessage("Apakah kamu yakin ingin menghapus semua data transaksi?")
            .setPositiveButton("Hapus") { _, _ -> viewModel.hapusSemuaTransaksi() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun exportDataToCsv() {
        lifecycleScope.launch {
            val transactions = viewModel.transactions
                .filter { it.isNotEmpty() }
                .first()

            val fileName = "export-transaksi-${System.currentTimeMillis()}.csv"
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

            val success = TransactionCsvHelper.exportToCsv(transactions, file)

            Toast.makeText(requireContext(), if (success) "Data berhasil diekspor" else "Gagal mengekspor data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Pilih File CSV"), PICK_CSV_FILE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_CSV_FILE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            if (uri.path?.endsWith(".csv", true) == true) {
                importTransactionsFromUri(uri)
            } else {
                Toast.makeText(requireContext(), "File bukan CSV", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun importTransactionsFromUri(uri: Uri) {
        lifecycleScope.launch {
            try {
                val transactions = TransactionCsvHelper.importFromCsv(requireContext(), uri)
                transactions.forEach { viewModel.tambahTransaksi(it) }
                Toast.makeText(requireContext(), "Import berhasil: ${transactions.size} transaksi", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal mengimpor data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCurrencySelectionDialog() {
        val options = arrayOf("Rupiah", "Dollar", "Euro")
        val symbols = arrayOf("Rp", "$", "€")

        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Mata Uang")
            .setItems(options) { _, index ->
                lifecycleScope.launch {
                    userPreference.setCurrencySymbol(symbols[index])
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}