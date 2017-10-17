package com.cray.software.passwords.notes;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cray.software.passwords.databinding.NoteListItemBinding;
import com.cray.software.passwords.interfaces.SimpleListener;

/**
 * Copyright 2017 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class NoteHolder extends RecyclerView.ViewHolder {

    public NoteListItemBinding binding;
    private SimpleListener mEventListener;

    public NoteHolder(View v, SimpleListener listener) {
        super(v);
        this.mEventListener = listener;
        binding = DataBindingUtil.bind(v);
        v.setOnClickListener(v1 -> {
            if (mEventListener != null) {
                mEventListener.onItemClicked(getAdapterPosition(), v1);
            }
        });
        v.setOnLongClickListener(v12 -> {
            if (mEventListener != null) {
                mEventListener.onItemLongClicked(getAdapterPosition(), v12);
            }
            return true;
        });
    }
}
