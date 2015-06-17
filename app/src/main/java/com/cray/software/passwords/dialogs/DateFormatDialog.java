package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cray.software.passwords.R;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;

public class DateFormatDialog extends Activity {

    SharedPrefs sPrefs;
    ListView musicList;
    Button musicDialogOk;
    TextView dialogTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list_dilog);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogTitle = (TextView) findViewById(R.id.dialogTitle);

        musicList = (ListView) findViewById(R.id.musicList);
        musicList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.date_formats, android.R.layout.select_dialog_singlechoice);
        musicList.setAdapter(aa);

        sPrefs = new SharedPrefs(DateFormatDialog.this);
        int type = sPrefs.loadInt(Constants.NEW_PREFERENCES_DATE_FORMAT);
        int position;
        if (type == Constants.DATE_EU){
            position = 0;
        } else if (type == Constants.DATE_US){
            position = 1;
        } else if (type == Constants.DATE_CN){
            position = 2;
        } else {
            position = 0;
        }

        musicList.setItemChecked(position, true);

        musicDialogOk = (Button) findViewById(R.id.musicDialogOk);
        musicDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = musicList.getCheckedItemPosition();
                if (selectedPosition != -1) {
                    sPrefs = new SharedPrefs(DateFormatDialog.this);
                    if (selectedPosition == 0){
                        sPrefs.saveInt(Constants.NEW_PREFERENCES_DATE_FORMAT, Constants.DATE_EU);
                    } else if (selectedPosition == 1){
                        sPrefs.saveInt(Constants.NEW_PREFERENCES_DATE_FORMAT, Constants.DATE_US);
                    } else if (selectedPosition == 2){
                        sPrefs.saveInt(Constants.NEW_PREFERENCES_DATE_FORMAT, Constants.DATE_CN);
                    } else {
                        sPrefs.saveInt(Constants.NEW_PREFERENCES_DATE_FORMAT, Constants.DATE_EU);
                    }
                    finish();
                } else {
                    Toast.makeText(DateFormatDialog.this, getString(R.string.list_item_warming), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
