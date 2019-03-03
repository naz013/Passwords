package com.cray.software.passwords.passwords

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.FragmentPasswordsBinding
import com.cray.software.passwords.fragments.BaseFragment
import com.cray.software.passwords.helpers.DataProvider
import com.cray.software.passwords.helpers.ListInterface
import com.cray.software.passwords.helpers.Utils
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.interfaces.SimpleListener
import com.cray.software.passwords.tasks.DeleteTask
import com.cray.software.passwords.utils.Dialogues
import com.cray.software.passwords.utils.Prefs

/**
 * Copyright 2017 Nazar Suhovich
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class PasswordsFragment : BaseFragment(), SimpleListener {

    private var binding: FragmentPasswordsBinding? = null
    private var adapter: PasswordsRecyclerAdapter? = null

    protected override val bgView: View?
        get() = binding!!.bgView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPasswordsBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.currentList.setLayoutManager(LinearLayoutManager(context))
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        loaderAdapter()
        if (anInterface != null) {
            anInterface!!.setTitle(getString(R.string.passwords))
            anInterface!!.setClick { view -> startActivity(Intent(context, ManagePassword::class.java)) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.fragment_passwords, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_order -> {
                showOrders()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showOrders() {
        if (context == null) return
        val items = arrayOf<CharSequence>(getString(R.string.sort_by_date_az), getString(R.string.sort_by_date_za), getString(R.string.sort_by_title_az), getString(R.string.sort_by_title_za))
        val builder = Dialogues.getDialog(context!!)
        builder.setTitle(getString(R.string.menu_sort_title))
        builder.setItems(items) { dialog, item ->
            val prefs = Prefs.getInstance(context)
            if (item == 0) {
                prefs.orderBy = Constants.ORDER_DATE_A_Z
            } else if (item == 1) {
                prefs.orderBy = Constants.ORDER_DATE_Z_A
            } else if (item == 2) {
                prefs.orderBy = Constants.ORDER_TITLE_A_Z
            } else if (item == 3) {
                prefs.orderBy = Constants.ORDER_TITLE_Z_A
            }
            dialog.dismiss()
            loaderAdapter()
        }
        val alert = builder.create()
        alert.show()
    }

    fun loaderAdapter() {
        adapter = PasswordsRecyclerAdapter(DataProvider.getData(context, true, false))
        adapter!!.setEventListener(this)
        binding!!.currentList.setAdapter(adapter)
        updateEmptyView()
    }

    private fun updateEmptyView() {
        if (adapter!!.itemCount > 0) {
            binding!!.emptyItem.visibility = View.GONE
        } else {
            binding!!.emptyItem.visibility = View.VISIBLE
        }
    }

    override fun onItemClicked(position: Int, view: View) {
        when (view.id) {
            R.id.more_button -> showPopup(position, view)
            else -> edit(position)
        }
    }

    private fun showPopup(position: Int, view: View) {
        val el = adapter!!.getItem(position)
        if (el is PasswordListInterface) {
            val items = arrayOf(getString(R.string.copy_login), getString(R.string.copy_password), getString(R.string.edit), getString(R.string.delete), getString(R.string.delete_with_backup))
            Utils.showLCAM(context, view, { item ->
                when (item) {
                    0 -> copyText(getString(R.string.item_login), el.login)
                    1 -> copyText(getString(R.string.item_password), el.password)
                    2 -> edit(position)
                    3 -> delete(position, false)
                    4 -> delete(position, true)
                }
            }, *items)
        }
    }

    private fun copyText(label: String, value: String) {
        if (activity == null) return
        val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        if (clipboard != null) {
            clipboard.primaryClip = clip
            Toast.makeText(context, label + " " + getString(R.string.copied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun edit(position: Int) {
        val item = adapter!!.getItem(position)
        val id = item.id
        if (item is PasswordListInterface) {
            val intentId = Intent(context, ManagePassword::class.java)
            intentId.putExtra(Constants.INTENT_ID, id)
            startActivity(intentId)
        }
    }

    private fun delete(position: Int, deleteFile: Boolean) {
        val item = adapter!!.getItem(position)
        val del = item.id
        if (item is PasswordListInterface) {
            DeleteTask(context, null).execute(del, if (deleteFile) 1L else 0L)
        }
        adapter!!.remove(position)
        updateEmptyView()
    }

    companion object {

        val TAG = "PasswordsFragment"

        fun newInstance(): PasswordsFragment {
            return PasswordsFragment()
        }
    }
}
