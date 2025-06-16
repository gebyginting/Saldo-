package com.geby.saldo.ui.kategori

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.geby.saldo.R
import com.geby.saldo.data.adapter.CategoryAdapter
import com.geby.saldo.data.model.Category
import com.geby.saldo.data.model.TransactionType
import com.geby.saldo.databinding.FragmentKategoriBinding
import com.geby.saldo.ui.viewmodel.KategoriViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory

class KategoriFragment : Fragment() {

    private var _binding: FragmentKategoriBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CategoryAdapter
    private val kategoriList = mutableListOf<Category>()
    private val viewModel: KategoriViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext().applicationContext)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKategoriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CategoryAdapter(kategoriList)
        binding.rvKategori.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvKategori.adapter = adapter

        viewModel.loadKategori() // Muat data kategori dari database

        viewModel.kategoriList.observe(viewLifecycleOwner) {
            kategoriList.clear()
            kategoriList.addAll(it)
            adapter.notifyDataSetChanged()
        }

        binding.tambahKategori.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tambah_kategori, null)
        val etNama = dialogView.findViewById<EditText>(R.id.etNamaKategori)
        val spinnerTipe = dialogView.findViewById<Spinner>(R.id.spinnerTipeKategori)
        val gridIcon = dialogView.findViewById<GridLayout>(R.id.gridIkonKategori)

        val icons = listOf(
            R.drawable.ic_food,
            R.drawable.ic_transport,
            R.drawable.ic_income,
            R.drawable.ic_education,
            R.drawable.ic_kategori_other
        )

        var selectedIcon = icons.first()

        // Buat grid ikon
        icons.forEach { iconRes ->
            val imageView = ImageView(requireContext()).apply {
                setImageResource(iconRes)
                setPadding(16, 16, 16, 16)
                layoutParams = ViewGroup.LayoutParams(150, 150)
                setOnClickListener {
                    selectedIcon = iconRes
                    // (Opsional: tambahkan highlight visual)
                }
            }
            gridIcon.addView(imageView)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Kategori")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = etNama.text.toString()
                val tipe = when (spinnerTipe.selectedItem.toString()) {
                    "Pemasukan" -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }

                if (nama.isNotBlank()) {
                    val newCategory = Category(
                        name = nama,
                        iconResId = selectedIcon,
                        type = tipe,)
                    viewModel.tambahKategori(newCategory)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Dummy data awal
//kategoriList.add(Category("Transport", R.drawable.ic_transport, TransactionType.EXPENSE))
//kategoriList.add(Category("Makan", R.drawable.ic_food, TransactionType.EXPENSE))
//kategoriList.add(Category("Pemasukan", R.drawable.ic_income, TransactionType.EXPENSE))
//kategoriList.add(Category("Pendidikan", R.drawable.ic_education, TransactionType.EXPENSE))
