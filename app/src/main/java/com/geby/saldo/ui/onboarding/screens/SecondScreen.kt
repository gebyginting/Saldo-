package com.geby.saldo.ui.onboarding.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geby.saldo.R
import com.geby.saldo.databinding.FragmentSecondScreenBinding
import com.geby.saldo.ui.viewmodel.UserViewModel
import com.geby.saldo.ui.viewmodel.ViewModelFactory
import com.geby.saldo.utils.Helper.cleanSaldo
import com.geby.saldo.utils.Helper.setupCurrencyFormatter
import kotlinx.coroutines.launch

class SecondScreen : Fragment() {

    private var _binding: FragmentSecondScreenBinding? = null
    private val binding get() = _binding!!
    private val factory: ViewModelFactory by lazy { ViewModelFactory.getInstance(requireContext()) }
    private val userViewModel: UserViewModel by viewModels { factory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        setupButtons()
        setupCurrencyFormatter(binding.etSaldoAwal)
        return view
    }

    private fun setupButtons() {
        binding.btnSelanjutnya.setOnClickListener {
            setupUserPreference()
        }
    }

    private fun setupUserPreference() {
        val name = binding.etNama.text.toString()
        val saldo = cleanSaldo(binding.etSaldoAwal.text.toString())
        if (name.isNotEmpty() && saldo != null) {
            lifecycleScope.launch {
                userViewModel.saveUser(name, saldo)
                onBoardingFinished()
                findNavController().navigate(R.id.action_onboarding_to_main)
            }
        } else {
            Toast.makeText(requireContext(), "Isi semua field", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

}