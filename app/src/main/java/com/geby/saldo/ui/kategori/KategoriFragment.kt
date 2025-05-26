package com.geby.saldo.ui.kategori

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.geby.saldo.R
import com.geby.saldo.data.adapter.CategoryAdapter
import com.geby.saldo.data.model.Category
import com.geby.saldo.databinding.FragmentKategoriBinding

class KategoriFragment : Fragment() {

    private var _binding: FragmentKategoriBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CategoryAdapter
    private val kategoriList = mutableListOf<Category>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKategoriBinding.inflate(inflater, container, false)

        adapter = CategoryAdapter(kategoriList)
        binding.rvKategori.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvKategori.adapter = adapter

        // Dummy data awal
        kategoriList.add(Category("Transport", R.drawable.ic_transport))
        kategoriList.add(Category("Makan", R.drawable.ic_food))
        kategoriList.add(Category("Pemasukan", R.drawable.ic_income))
        kategoriList.add(Category("Pendidikan", R.drawable.ic_education))

        binding.tambahKategori.setOnClickListener {
            showAddCategoryDialog()
        }

        return binding.root
    }

    private fun showAddCategoryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tambah_kategori, null)
        val etNama = dialogView.findViewById<EditText>(R.id.etNamaKategori)

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Kategori")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = etNama.text.toString()
                if (nama.isNotBlank()) {
                    kategoriList.add(Category(nama, R.drawable.ic_kategori_other))
                    adapter.notifyItemInserted(kategoriList.size - 1)
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
