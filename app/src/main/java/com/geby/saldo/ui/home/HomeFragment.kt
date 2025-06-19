package com.geby.saldo.ui.home

import android.graphics.Color
import android.graphics.Typeface
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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionAdapter: TransactionAdapter
    private val factory: ViewModelFactory by lazy { ViewModelFactory.getInstance(requireContext()) }
    private val userViewModel: UserViewModel by viewModels { factory }
    private val viewModel: TransactionViewModel by viewModels { factory }

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saldo.collect { saldo ->
                    binding.textSaldo.text = Helper.formatRupiahTanpaKoma(saldo)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.totalPemasukan.collect {
                        binding.tvTotalIncome.text = Helper.formatRupiahTanpaKoma(it)
                    }
                }
                launch {
                    viewModel.totalPengeluaran.collect {
                        binding.tvTotalExpense.text = Helper.formatRupiahTanpaKoma(it)
                    }
                }
            }
        }
    }

    private fun setupPieChart(income: Float, expense: Float) {
        // Membuat list data untuk pie chart, berupa PieEntry yang berisi nilai dan label
        val entries = ArrayList<PieEntry>()

        // Tambahkan data pemasukan jika lebih dari 0, dengan label "Pemasukan"
        if (income > 0) entries.add(PieEntry(income, "Pemasukan"))
        // Tambahkan data pengeluaran jika lebih dari 0, dengan label "Pengeluaran"
        if (expense > 0) entries.add(PieEntry(expense, "Pengeluaran"))

        // Membuat dataset dari entries, label dataset dikosongkan ("")
        val dataSet = PieDataSet(entries, "")

        // Custom warna untuk setiap slice pada pie chart
        val customColors = entries.map { entry ->
            when (entry.label) {
                "Pemasukan" -> Color.parseColor("#4CAF50") // Hijau
                "Pengeluaran" -> Color.parseColor("#F44336") // Merah
                else -> Color.GRAY // Default kalau ada label lain
            }
        }
        dataSet.colors = customColors

        // Ukuran teks nilai (persentase) di dalam slice
        dataSet.valueTextSize = 12f
        // Warna teks nilai di dalam slice
        dataSet.valueTextColor = Color.WHITE
        // Typeface untuk teks nilai agar tebal (bold)
        dataSet.valueTypeface = Typeface.DEFAULT_BOLD
        // Jarak antar potongan slice (agar tidak menempel)
        dataSet.sliceSpace = 2f

        // Pastikan nilai persentase ditampilkan di dalam slice
        dataSet.setDrawValues(true)

        // Mengatur agar data pada pie chart menggunakan nilai persentase (bukan nilai mentah)
        binding.pieChart.setUsePercentValues(true)

        // Membuat objek PieData dari dataSet, lalu mengatur formatter supaya nilai ditampilkan dalam format persen
        binding.pieChart.data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(binding.pieChart))
        }

        // Matikan deskripsi default yang biasanya muncul di bawah chart
        binding.pieChart.description.isEnabled = false

        // Set teks di tengah pie chart
        binding.pieChart.centerText = "Keuangan"
        // Ukuran teks di tengah pie chart
        binding.pieChart.setCenterTextSize(14f)

        // Jangan tampilkan label kategori (Pemasukan/Pengeluaran) di dalam slice,
        // hanya tampilkan persentase saja
        binding.pieChart.setDrawEntryLabels(false)

        // Mengatur legend (keterangan warna) yang muncul di bawah pie chart
        val legend = binding.pieChart.legend
        legend.isEnabled = true // pastikan legend aktif
        legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.CENTER // posisi vertikal legend
        legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT // posisi horizontal legend
        legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL// orientasi legend (horizontal)
        legend.setDrawInside(false) // legend ditampilkan di luar chart, bukan di dalam
        legend.textSize = 12f // ukuran teks legend
        legend.form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE // bentuk simbol legend berupa lingkaran
        legend.formSize = 12f // ukuran simbol legend
        legend.xEntrySpace = 20f // jarak horizontal antar entri legend
        legend.yOffset = 10f  // coba angka negatif seperti ini untuk mendekatkan legend ke pie chart

        // Aktifkan lingkaran "hole" (lubang tengah) pada pie chart
        binding.pieChart.isDrawHoleEnabled = true
        // Warna lingkaran lubang tengah
        binding.pieChart.setHoleColor(Color.WHITE)
        // Menghilangkan lingkaran transparan di sekitar hole (agar lebih bersih)
        binding.pieChart.setTransparentCircleAlpha(0)

        // Animasi saat pie chart muncul, arah vertikal selama 1000ms (1 detik)
        binding.pieChart.animateY(1000)

        // Memanggil invalidate supaya pie chart di-refresh dan ditampilkan ulang
        binding.pieChart.invalidate()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
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
                        setupPieChart(0f, 0f)
                        return@collect
                    }

                    binding.emptyTransactionMsg.visibility = View.GONE

                    val recentList = transactionList.sortedByDescending { it.date }.take(3)
                    transactionAdapter.submitList(recentList)

                    val income = transactionList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val expense = transactionList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                    setupPieChart(income.toFloat(), expense.toFloat())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
