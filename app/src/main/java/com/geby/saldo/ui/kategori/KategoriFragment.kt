package com.geby.saldo.ui.kategori

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.geby.saldo.data.adapter.CategoryAdapter
import com.geby.saldo.data.model.Category
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

        viewModel.kategoriList.observe(viewLifecycleOwner) { it ->
            kategoriList.clear()
            kategoriList.addAll(it)

            // ⬇️ Sort dengan kategori "other" di paling bawah
            val (otherCategories, normalCategories) = kategoriList.partition { it.isOtherCategory }
            val finalList = normalCategories.sortedBy { it.name } + otherCategories

            adapter = CategoryAdapter(finalList)
            binding.rvKategori.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.rvKategori.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        viewModel.loadKategori()

        binding.tambahKategori.setOnClickListener {
            AddCategoryBottomSheetFragment()
                .show(parentFragmentManager, AddCategoryBottomSheetFragment::class.java.simpleName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
