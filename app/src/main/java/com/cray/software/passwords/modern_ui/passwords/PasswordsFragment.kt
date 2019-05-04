package com.cray.software.passwords.modern_ui.passwords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cray.software.passwords.databinding.PasswordsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PasswordsFragment : Fragment() {

    private val viewModel: PasswordsViewModel by viewModel()
    private lateinit var binding: PasswordsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = PasswordsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
    }
}
