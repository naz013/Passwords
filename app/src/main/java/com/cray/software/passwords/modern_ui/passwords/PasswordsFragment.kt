package com.cray.software.passwords.modern_ui.passwords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cray.software.passwords.databinding.PasswordsFragmentBinding
import com.cray.software.passwords.helpers.ListInterface
import com.cray.software.passwords.modern_ui.list.DataAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PasswordsFragment : Fragment() {

    private val viewModel: PasswordsViewModel by viewModel()
    private lateinit var binding: PasswordsFragmentBinding
    private val adapter = DataAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = PasswordsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        binding.addButton.setOnClickListener { findNavController().navigate(PasswordsFragmentDirections.actionPasswordsFragmentToCreatePasswordFragment()) }

        binding.dataList.layoutManager = LinearLayoutManager(context)
        binding.dataList.adapter = adapter

        viewModel.liveData.observe(this, Observer {
            if (it != null) {
                showData(it)
            }
        })
    }

    private fun showData(list: List<ListInterface>) {
        adapter.setData(list)
    }
}
