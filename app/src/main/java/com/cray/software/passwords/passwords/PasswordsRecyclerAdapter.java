package com.cray.software.passwords.passwords;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.ColorSetter;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.interfaces.SimpleListener;

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
public class PasswordsRecyclerAdapter extends RecyclerView.Adapter<PasswordsRecyclerAdapter.ViewHolder>{

    public static final int PASSWORD = 0;
    public static final int NOTE = 1;

    private List<PasswordListInterface> list;
    private Typeface typeface;
    private SimpleListener mEventListener;
    private ColorSetter mColor;

    public PasswordsRecyclerAdapter(Context context, List<PasswordListInterface> list) {
        this.list = list;
        this.mColor = new ColorSetter(context);
        this.typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PasswordListInterface item = list.get(position);
        holder.textView.setText(item.getTitle());
        holder.dateView.setText(item.getDate());
        holder.loginView.setText(item.getLogin());
        holder.itemCard.setCardBackgroundColor(mColor.getColor(mColor.colorPrimary(item.getColor())));
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setEventListener(SimpleListener eventListener) {
        mEventListener = eventListener;
    }
}