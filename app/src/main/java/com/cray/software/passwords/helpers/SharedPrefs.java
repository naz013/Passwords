package com.cray.software.passwords.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.cray.software.passwords.interfaces.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class SharedPrefs {
    SharedPreferences prefs;
    Context pContext;
    private static int MODE = Context.MODE_MULTI_PROCESS;
    public SharedPrefs(Context context){
        this.pContext = context;
    }

    public void savePrefs(String stringToSave, String value){
        prefs = pContext.getSharedPreferences(Constants.NEW_PREFERENCES, MODE);
        SharedPreferences.Editor uiEd = prefs.edit();
        uiEd.putString(stringToSave, value);
        uiEd.commit();
    }

    public void saveInt(String stringToSave, int value){
        prefs = pContext.getSharedPreferences(Constants.NEW_PREFERENCES, MODE);
        SharedPreferences.Editor uiEd = prefs.edit();
        uiEd.putInt(stringToSave, value);
        uiEd.commit();
    }

    public int loadInt(String stringToLoad){
        prefs = pContext.getSharedPreferences(Constants.NEW_PREFERENCES, MODE);
        int x;
        try {
            x = prefs.getInt(stringToLoad, 4);
        } catch (ClassCastException e) {
            x = Integer.parseInt(prefs.getString(stringToLoad, "4"));
        }
        return x;
    }

    public String loadPrefs(String stringToLoad){
        try {
            prefs = pContext.getSharedPreferences(Constants.NEW_PREFERENCES, MODE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return prefs.getString(stringToLoad, "");
    }

    public boolean isString(String checkString){
        prefs = pContext.getSharedPreferences(Constants.NEW_PREFERENCES, MODE);
        return prefs.contains(checkString);
    }

    public void saveBoolean(String stringToSave, boolean value){
        prefs = pContext.getSharedPreferences(Constants.NEW_PREFERENCES, MODE);
        SharedPreferences.Editor uiEd = prefs.edit();
        uiEd.putBoolean(stringToSave, value);
        uiEd.commit();
    }
    public boolean loadBoolean(String stringToLoad){
		prefs = pContext.getSharedPreferences(Constants.NEW_PREFERENCES, MODE);
		boolean res;
		try {
		res = prefs.getBoolean(stringToLoad, false);
		} catch (ClassCastException e){
		res = Boolean.parseBoolean(prefs.getString(stringToLoad, "false"));
		}
		return res;
    }

    public void savePassPrefs(String value){
        prefs = pContext.getSharedPreferences(Constants.NEW_APP_PREFS, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor uiEd = prefs.edit();
        uiEd.putString(Constants.NEW_APP_PREFERENCES_LOGIN, value);
        uiEd.commit();
    }

    public String loadPassPrefs(){
        prefs = pContext.getSharedPreferences(Constants.NEW_APP_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(Constants.NEW_APP_PREFERENCES_LOGIN, "1111");
    }

    public boolean isPassString(){
        prefs = pContext.getSharedPreferences(Constants.NEW_APP_PREFS, Context.MODE_PRIVATE);
        return prefs.contains(Constants.NEW_APP_PREFERENCES_LOGIN);
    }

    public void saveSystemPrefs(String key, String value){
        prefs = pContext.getSharedPreferences(Constants.NEW_APP_PREFS, MODE);
        SharedPreferences.Editor uiEd = prefs.edit();
        uiEd.putString(key, value);
        uiEd.commit();
    }

    public String loadSystemPrefs(String key){
        prefs = pContext.getSharedPreferences(Constants.NEW_APP_PREFS, MODE);
        return prefs.getString(key, Constants.DRIVE_USER_NONE);
    }

    public boolean isSystemKey(String key){
        prefs = pContext.getSharedPreferences(Constants.NEW_APP_PREFS, MODE);
        return prefs.contains(key);
    }

    public boolean saveSharedPreferencesToFile(File dst) {
        boolean res = false;
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(dst));
            SharedPreferences pref =
                    pContext.getSharedPreferences(Constants.NEW_PREFERENCES, Context.MODE_PRIVATE);
            output.writeObject(pref.getAll());

            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    @SuppressWarnings({ "unchecked" })
    public boolean loadSharedPreferencesFromFile(File src) {
        boolean res = false;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(src));
            SharedPreferences.Editor prefEdit = pContext.getSharedPreferences(Constants.NEW_PREFERENCES, Context.MODE_PRIVATE).edit();
            prefEdit.clear();
            Map<String, ?> entries = (Map<String, ?>) input.readObject();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    prefEdit.putBoolean(key, (Boolean) v);
                else if (v instanceof Float)
                    prefEdit.putFloat(key, (Float) v);
                else if (v instanceof Integer)
                    prefEdit.putInt(key, (Integer) v);
                else if (v instanceof Long)
                    prefEdit.putLong(key, (Long) v);
                else if (v instanceof String)
                    prefEdit.putString(key, ((String) v));
            }
            prefEdit.commit();
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }
}
