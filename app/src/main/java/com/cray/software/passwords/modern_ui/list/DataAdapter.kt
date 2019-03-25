package com.cray.software.passwords.modern_ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cray.software.passwords.R
import com.cray.software.passwords.databinding.ListItemNoteBinding
import com.cray.software.passwords.databinding.ListItemPasswordBinding
import com.cray.software.passwords.helpers.ListInterface

class DataAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mData: MutableList<ListInterface> = mutableListOf()

    fun setData(list: List<ListInterface>) {
        mData.clear()
        mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NOTE -> NoteHolder(parent)
            else -> PasswordHolder(parent)
        }
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PasswordHolder) {
            holder.bind(mData[position])
        } else if (holder is NoteHolder) {
            holder.bind(mData[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mData[position].viewType
    }

    inner class PasswordHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_password, parent, false)) {

        private val binding: ListItemPasswordBinding = DataBindingUtil.bind(itemView)!!

        fun bind(model: ListInterface) {

        }
    }

    inner class NoteHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_note, parent, false)) {

        private val binding: ListItemNoteBinding = DataBindingUtil.bind(itemView)!!

        fun bind(model: ListInterface) {

        }
    }

    companion object {
        const val TYPE_NOTE = 1
        const val TYPE_PASSWORD = 0
    }
}