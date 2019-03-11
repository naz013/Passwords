package com.cray.software.passwords.passwords

import androidx.databinding.DataBindingUtil
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.cray.software.passwords.databinding.ListItemCardBinding
import com.cray.software.passwords.databinding.NoteListItemBinding
import com.cray.software.passwords.helpers.ListInterface
import com.cray.software.passwords.interfaces.Module
import com.cray.software.passwords.interfaces.SimpleListener
import com.cray.software.passwords.notes.NoteHolder
import com.cray.software.passwords.data.NoteListInterface
import com.cray.software.passwords.data.PasswordListInterface
import com.cray.software.passwords.utils.ThemeUtil

import java.util.ArrayList

/**
 * Copyright 2016 Nazar Suhovich
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
class PasswordsRecyclerAdapter(list: List<ListInterface>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = ArrayList<ListInterface>()
    private var mEventListener: SimpleListener? = null

    init {
        this.list.clear()
        this.list.addAll(list)
    }

    fun getItem(position: Int): ListInterface {
        return list[position]
    }

    fun remove(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(0, list.size)
    }

    inner class ViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        internal var binding: ListItemCardBinding? = null

        init {
            binding = DataBindingUtil.bind<T>(v)
            v.setOnClickListener { view ->
                if (mEventListener != null) {
                    mEventListener!!.onItemClicked(adapterPosition, binding!!.itemCard)
                }
            }
            binding!!.moreButton.setOnClickListener { view ->
                if (mEventListener != null) {
                    mEventListener!!.onItemClicked(adapterPosition, view)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == PASSWORD) {
            ViewHolder(ListItemCardBinding.inflate(inflater, parent, false).getRoot())
        } else {
            NoteHolder(NoteListItemBinding.inflate(inflater, parent, false).getRoot(), mEventListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = getItem(position) as PasswordListInterface
            holder.binding!!.textView.text = item.title
            holder.binding!!.dateView.text = item.date
            holder.binding!!.loginView.text = item.login
            val util = getTheme(holder.binding!!.itemCard)
            if (util != null) {
                holder.binding!!.itemCard.setCardBackgroundColor(util.getColor(util.colorPrimary(item.color)))
            }
        } else if (holder is NoteHolder) {
            val item = getItem(position) as NoteListInterface
            loadNote(holder.binding!!.note, item.title)
            loadNoteCard(holder.binding!!.card, item.color)
            setImage(holder.binding!!.noteImage, item.image)
        }
    }

    private fun loadNote(textView: TextView, summary: String) {
        var summary = summary
        if (TextUtils.isEmpty(summary)) {
            textView.visibility = View.GONE
            return
        }
        if (summary.length > 500) {
            val substring = summary.substring(0, 500)
            summary = "$substring..."
        }
        textView.text = summary
    }

    private fun getTheme(view: View): ThemeUtil? {
        return ThemeUtil.getInstance(view.context)
    }

    private fun loadNoteCard(cardView: CardView, color: Int) {
        val themeUtil = getTheme(cardView)
        if (themeUtil != null) {
            cardView.setCardBackgroundColor(themeUtil.getColor(themeUtil.colorPrimary(color)))
        }
        if (Module.isLollipop) {
            cardView.cardElevation = 4f
        }
    }

    private fun setImage(imageView: ImageView, image: ByteArray) {
        Glide.with(imageView.context)
                .load(image)
                .into(imageView)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setEventListener(eventListener: SimpleListener) {
        mEventListener = eventListener
    }

    companion object {

        val PASSWORD = 0
        val NOTE = 1
    }
}
