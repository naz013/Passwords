package com.cray.software.passwords.modern_ui.passwords

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cray.software.passwords.R

class PasswordsFragment : Fragment() {

    companion object {
        fun newInstance() = PasswordsFragment()
    }

    private lateinit var viewModel: PasswordsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.passwords_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PasswordsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
