package com.cray.software.passwords.modern_ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cray.software.passwords.databinding.HomeFragmentBinding
import com.cray.software.passwords.helpers.ListInterface
import com.cray.software.passwords.modern_ui.list.DataAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
    private val adapter = DataAdapter()
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSettings.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionActionHomeToSettingsFragment()) }
        binding.cardPasswords.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionActionHomeToActionPasswords()) }
        binding.cardNotes.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionActionHomeToActionNotes()) }

        binding.dataList.layoutManager = LinearLayoutManager(context)
        binding.dataList.adapter = adapter

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.homeLiveData.observe(this, Observer {
            if (it != null) {
                showData(it)
            }
        })
    }

    private fun showData(list: List<ListInterface>) {
        adapter.setData(list)
    }
}
