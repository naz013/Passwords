package com.cray.software.passwords;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.cray.software.passwords.helpers.ColorSetter;

import java.util.Locale;

public class ApplicationHelp extends ActionBarActivity implements View.OnClickListener, Html.ImageGetter {

    TextView help_title_1, help_text_1,
            help_title_2, help_text_2,
            help_title_3, help_text_3,
            help_title_6, help_text_6,
            help_title_8, help_text_8;
    ActionBar ab;
    ColorSetter cSetter;

    public final String help_add_item = "<p>To add new password click on " +
            "<img src = 'ic_add_grey600_24dp.png'>" +
            " at main screen.</p>" + "If You want save this password and clean fields for another, click " +
            "<img src='ic_done_all_grey600_24dp.png'>.";
    public final String help_delete = "To delete password swipe left/right item in list on main screen. Or open item and click " +
            "<img src='ic_delete_grey600_24dp.png'>" + ".";
    public final String help_edit = "To edit password open it and check this " + "<img src='grey_unchecked.png'>" +
            ". When You end editing click this " + "<img src='ic_done_grey600_24dp.png'> for saving.";
    public final String help_restore_key = "Restore keyword will needed when you forgot Your application login password." +
            " To use this keyword just insert wrong password at app login screen.";

    public final String help_add_item_uk = "<p>Для того, щоб додати пароль натисніть на цю " +
            "<img src = 'ic_add_grey600_24dp.png'>" +
            " кнопку на головному екрані.</p>" + "Якщо Ви хочете зберегти декілька паролів, то використовуйте цю кнопку " +
            "<img src='ic_done_all_grey600_24dp.png'>.";
    public final String help_delete_uk = "Для того щоб видалити пароль потягніть його вліво чи вправо на головному екрані. Або відкрийте його і натисніть " +
            "<img src='ic_delete_grey600_24dp.png'>" + ".";
    public final String help_edit_uk = "Для того, щоб редагувати пароль відкрийте його і натисніть на " +
            "<img src='grey_unchecked.png'>" +
            ". Коли завершите редагування натисніть " + "<img src='ic_done_grey600_24dp.png'> для збереження.";
    public final String help_restore_key_uk = "Ключ відновлення пароля беде необхідний коли Ви забудете пароль до програми." +
            " Щоб скористатися ключем введіть невірний пароль на екрані входу в програму.";

    public final String help_add_item_ru = "<p>Для того, чтобы добавить пароль нажмите на эту " +
            "<img src = 'ic_add_grey600_24dp.png'>" +
            " кнопку на главном экране.</p>" + "Если Вы хотите сохранить несколько паролей используйте эту кнопку " +
            "<img src='ic_done_all_grey600_24dp.png'>.";
    public final String help_delete_ru = "Для того чтобы удалить пароль на главном экране потяните его влеви или вправо. Или откройте его и нажмите " +
            "<img src='ic_delete_grey600_24dp.png'>" + ".";
    public final String help_edit_ru = "Для того, чтобы отредактировать пароль откройте его и нажмите на эту кнопку " +
            "<img src='grey_unchecked.png'>" +
            ". Когда закончите редактирование нажмите на эту кнопку " + "<img src='ic_done_grey600_24dp.png'> для сохранения.";
    public final String help_restore_key_ru = "Ключ восстановления пароля к программе будет нужен тогда, когда Вы забудете пароль к приложению." +
            " Чтобы воспользоватся им, просто введите неверный пароль на экране входа в программу.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        cSetter = new ColorSetter(ApplicationHelp.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayShowTitleEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayUseLogoEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.fragment_help);
            ab.setIcon(R.drawable.ic_help_white_24dp);
            viewSetter(ab);
        }

        String localeCheck = Locale.getDefault().toString();

        help_title_1 = (TextView) findViewById(R.id.help_title_1);
        help_title_1.setOnClickListener(this);

        help_text_1 = (TextView) findViewById(R.id.help_text_1);
        if(localeCheck.matches("uk_UA")){
            help_text_1.setText(Html.fromHtml(help_add_item_uk, this, null));
        } else  if (localeCheck.matches("ru_RU")){
            help_text_1.setText(Html.fromHtml(help_add_item_ru, this, null));
        } else {
            help_text_1.setText(Html.fromHtml(help_add_item, this, null));
        }
        help_text_1.setVisibility(View.GONE);

        help_title_2 = (TextView) findViewById(R.id.help_title_2);
        help_title_2.setOnClickListener(this);

        help_text_2 = (TextView) findViewById(R.id.help_text_2);
        if(localeCheck.matches("uk_UA")){
            help_text_2.setText(Html.fromHtml(help_delete_uk, this, null));
        } else  if (localeCheck.matches("ru_RU")){
            help_text_2.setText(Html.fromHtml(help_delete_ru, this, null));
        } else {
            help_text_2.setText(Html.fromHtml(help_delete, this, null));
        }
        help_text_2.setVisibility(View.GONE);

        help_title_3 = (TextView) findViewById(R.id.help_title_3);
        help_title_3.setOnClickListener(this);

        help_text_3 = (TextView) findViewById(R.id.help_text_3);
        if(localeCheck.matches("uk_UA")){
            help_text_3.setText(Html.fromHtml(help_edit_uk, this, null));
        } else  if (localeCheck.matches("ru_RU")){
            help_text_3.setText(Html.fromHtml(help_edit_ru, this, null));
        } else {
            help_text_3.setText(Html.fromHtml(help_edit, this, null));
        }
        help_text_3.setVisibility(View.GONE);

        help_title_6 = (TextView) findViewById(R.id.help_title_6);
        help_title_6.setOnClickListener(this);

        help_text_6 = (TextView) findViewById(R.id.help_text_6);
        if(localeCheck.matches("uk_UA")){
            help_text_6.setText(Html.fromHtml(help_restore_key_uk, this, null));
        } else  if (localeCheck.matches("ru_RU")){
            help_text_6.setText(Html.fromHtml(help_restore_key_ru, this, null));
        } else {
            help_text_6.setText(Html.fromHtml(help_restore_key, this, null));
        }
        help_text_6.setVisibility(View.GONE);

        help_title_8 = (TextView) findViewById(R.id.help_title_8);
        help_title_8.setOnClickListener(this);

        help_text_8 = (TextView) findViewById(R.id.help_text_8);
        help_text_8.setText(R.string.help_use_answer);
        help_text_8.setVisibility(View.GONE);
    }

    @Override
    public Drawable getDrawable(String arg0) {
        int id = 0;
        if (arg0.equals("ic_add_grey600_24dp.png")) {
            id = R.drawable.ic_add_grey600_24dp;
        }
        if (arg0.equals("ic_delete_grey600_24dp.png")) {
            id = R.drawable.ic_delete_grey600_24dp;
        }
        if(arg0.equals("ic_done_all_grey600_24dp.png")){
            id = R.drawable.ic_done_all_grey600_24dp;
        }
        if(arg0.equals("ic_done_grey600_24dp.png")){
            id = R.drawable.ic_done_grey600_24dp;
        }
        if(arg0.equals("grey_unchecked.png")) {
            id = R.drawable.grey_unchecked;
        }
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = getResources().getDrawable(id);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

        return d;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    private void viewSetter(ActionBar ab){
        cSetter = new ColorSetter(ApplicationHelp.this);
        ab.setBackgroundDrawable(new ColorDrawable(cSetter.colorSetter()));
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.help_title_1:
                toggle_contents(help_text_1);
                break;
            case R.id.help_title_2:
                toggle_contents(help_text_2);
                break;
            case R.id.help_title_3:
                toggle_contents(help_text_3);
                break;
            case R.id.help_title_6:
                toggle_contents(help_text_6);
                break;
            case R.id.help_title_8:
                toggle_contents(help_text_8);
                break;
        }
    }

    public void toggle_contents(View v){
        if(v.isShown()){
            slide_up(this, v);
            v.setVisibility(View.GONE);
        }
        else{
            v.setVisibility(View.VISIBLE);
            slide_down(this, v);
        }
    }

    public static void slide_down(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.expand_item);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.collapse_item);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }
}
