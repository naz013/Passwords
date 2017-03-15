package com.cray.software.passwords.passwords;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cray.software.passwords.R;
import com.cray.software.passwords.databinding.NoteListItemBinding;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.helpers.ListInterface;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.interfaces.SimpleListener;
import com.cray.software.passwords.notes.NoteHolder;
import com.cray.software.passwords.notes.NoteListInterface;

import java.util.List;

/**
 * Copyright 2016 Nazar Suhovich
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
public class PasswordsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int PASSWORD = 0;
    public static final int NOTE = 1;

    private List<ListInterface> list;
    private Typeface typeface;
    private SimpleListener mEventListener;
    private ColorSetter mColor;

    public PasswordsRecyclerAdapter(Context context, List<ListInterface> list) {
        this.list = list;
        this.mColor = new ColorSetter(context);
        this.typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
    }

    public ListInterface getItem(int position) {
        return list.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnLongClickListener, View.OnClickListener {

        public TextView textView, dateView, loginView;
        public CardView itemCard;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.textView);
            dateView = (TextView) v.findViewById(R.id.dateView);
            loginView = (TextView) v.findViewById(R.id.loginView);
            itemCard = (CardView) v.findViewById(R.id.itemCard);
            if (Module.isLollipop()) {
                itemCard.setCardElevation(5f);
            }

            textView.setTypeface(typeface);
            dateView.setTypeface(typeface);
            loginView.setTypeface(typeface);

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mEventListener != null) {
                mEventListener.onItemClicked(getAdapterPosition(), itemCard);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mEventListener != null) {
                mEventListener.onItemLongClicked(getAdapterPosition(), itemCard);
            }
            return true;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == PASSWORD) {
            View itemLayoutView = inflater.inflate(R.layout.list_item_card, parent, false);
            return new ViewHolder(itemLayoutView);
        } else {
            return new NoteHolder(NoteListItemBinding.inflate(inflater, parent, false).getRoot(), mEventListener);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder h = (ViewHolder) holder;
            PasswordListInterface item = (PasswordListInterface) getItem(position);
            h.textView.setText(item.getTitle());
            h.dateView.setText(item.getDate());
            h.loginView.setText(item.getLogin());
            h.itemCard.setCardBackgroundColor(mColor.getColor(mColor.colorPrimary(item.getColor())));
        } else if (holder instanceof NoteHolder) {
            NoteHolder h = (NoteHolder) holder;
            NoteListInterface item = (NoteListInterface) getItem(position);
            loadNote(h.binding.note, item.getSummary());
            loadNoteCard(h.binding.card, item.getColor());
            setImage(h.binding.noteImage, item.getImage());
        }
    }

    private void loadNote(TextView textView, String summary) {
        if (TextUtils.isEmpty(summary)) {
            textView.setVisibility(View.GONE);
            return;
        }
        if (summary.length() > 500) {
            String substring = summary.substring(0, 500);
            summary = substring + "...";
        }
        textView.setText(summary);
    }

    private void loadNoteCard(CardView cardView, int color) {
        cardView.setCardBackgroundColor(mColor.getColor(mColor.colorPrimary(color)));
        if (Module.isLollipop()) {
            cardView.setCardElevation(4f);
        }
    }

    private void setImage(ImageView imageView, byte[] image) {
        Glide.with(imageView.getContext().getApplicationContext())
                .load(image)
                .crossFade()
                .override(768, 500)
                .into(imageView);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setEventListener(SimpleListener eventListener) {
        mEventListener = eventListener;
    }
}
