package com.cray.software.passwords.helpers;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.cray.software.passwords.R;
import com.cray.software.passwords.interfaces.Constants;

public class CustomCursorAdapter extends CursorAdapter implements Filterable {

    TextView titleText, dateText;
    LayoutInflater inflater;
    String title_decrypted, date_decrypted;
    private Cursor c;
    Context cContext;
    DataBase DB;
    Crypter crypter;

    @SuppressWarnings("deprecation")
    public CustomCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.cContext = context;
        inflater = LayoutInflater.from(context);
        this.c = c;
        c.moveToFirst();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.item, null);
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndex("_id"));
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        c.moveToPosition(position);
        DB = new DataBase(cContext);
        crypter = new Crypter();
        if (convertView == null) {
            inflater = (LayoutInflater) cContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, null);
        }

        int colorCircle;
        String colorDB = c.getString(c.getColumnIndex(Constants.COLUMN_TECHNICAL));
        if (colorDB == null){
            colorCircle = cContext.getResources().getColor(R.color.colorSemiTrGrayDark);
        }else {
            colorCircle = Integer.parseInt(colorDB);
        }

        CardView itemLayout = (CardView) convertView.findViewById(R.id.itemLayout);
        itemLayout.setCardBackgroundColor(colorCircle);

        titleText = (TextView) convertView.findViewById(R.id.titleView);
        String title = c.getString(c.getColumnIndex(Constants.COLUMN_TITLE));
        title_decrypted = crypter.decrypt(title);
        titleText.setText(title_decrypted);

        dateText = (TextView) convertView.findViewById(R.id.dateView);
        String date = c.getString(c.getColumnIndex(Constants.COLUMN_DATE));
        date_decrypted = crypter.decrypt(date);
        dateText.setText(date_decrypted);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
