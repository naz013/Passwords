package com.cray.software.passwords.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.cray.software.passwords.interfaces.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncHelper {

    Context sContext;

    DataBase DB;

    public SyncHelper(Context context){
        this.sContext = context;
    }

    public void exportPasswords() throws JSONException, IOException {
        DB = new DataBase(sContext);
        DB.open();
        Cursor c = DB.fetchAllPasswords();
        if (c != null && c.moveToFirst()){
            do {
                long id = c.getLong(c.getColumnIndex(Constants.COLUMN_ID));
                String title = c.getString(c.getColumnIndex(Constants.COLUMN_TITLE));
                String login = c.getString(c.getColumnIndex(Constants.COLUMN_LOGIN));
                String password = c.getString(c.getColumnIndex(Constants.COLUMN_PASSWORD));
                String url = c.getString(c.getColumnIndex(Constants.COLUMN_URL));
                String comment = c.getString(c.getColumnIndex(Constants.COLUMN_COMMENT));
                String date = c.getString(c.getColumnIndex(Constants.COLUMN_DATE));
                String color = c.getString(c.getColumnIndex(Constants.COLUMN_TECHNICAL));
                String uuID = c.getString(c.getColumnIndex(Constants.COLUMN_PIC_SEL));

                if (uuID == null) {
                    uuID = generateID();
                    DB.setUniqueId(id, uuID);
                }

                JSONObject jObjectData = new JSONObject();
                jObjectData.put(Constants.COLUMN_ID, id);
                jObjectData.put(Constants.COLUMN_TITLE, title);
                jObjectData.put(Constants.COLUMN_LOGIN, login);
                jObjectData.put(Constants.COLUMN_PASSWORD, password);
                jObjectData.put(Constants.COLUMN_URL, url);
                jObjectData.put(Constants.COLUMN_COMMENT, comment);
                jObjectData.put(Constants.COLUMN_DATE, date);
                jObjectData.put(Constants.COLUMN_TECHNICAL, color);
                jObjectData.put(Constants.COLUMN_PIC_SEL, uuID);

                if (isSdPresent()) {
                    File sdPath = Environment.getExternalStorageDirectory();
                    File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
                    if (!sdPathDr.exists()) {
                        sdPathDr.mkdirs();
                    }
                    String exportFileName = uuID + Constants.FILE_EXTENSION;

                    File file = new File(sdPathDr, exportFileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    FileWriter fw = new FileWriter(file);
                    fw.write(jObjectData.toString());
                    fw.close();
                } else Log.i("reminder-info", "Couldn't find external storage!");
            } while (c.moveToNext());
        }
        if (c != null) {
            c.close();
        }
        DB.close();
    }

    public void importPasswordFromJSON() throws IOException, JSONException {
        if (isSdPresent()){
            DB = new DataBase(sContext);
            DB.open();
            List<String> namesPass = new ArrayList<String>();
            Cursor e = DB.fetchAllPasswords();
            while (e.moveToNext()) {
                for (e.moveToFirst(); !e.isAfterLast(); e.moveToNext()) {
                    namesPass.add(e.getString(e.getColumnIndex(Constants.COLUMN_PIC_SEL)));
                }
            }
            e.close();
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
            File[] files = sdPathDr.listFiles();
            if (files != null) {
                int f = files.length;
                if (f > 0) {
                    for (File file1 : files) {
                        String fileName = file1.getName();
                        int pos = fileName.lastIndexOf(".");
                        String fileLoc = sdPathDr + "/" + fileName;
                        String fileNameS = fileName.substring(0, pos);
                        if (!namesPass.contains(fileNameS)) {
                            FileInputStream stream = new FileInputStream(fileLoc);
                            Writer writer = new StringWriter();
                            char[] buffer = new char[1024];
                            try {
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(stream, "UTF-8")
                                );
                                int n;
                                while ((n = reader.read(buffer)) != -1) {
                                    writer.write(buffer, 0, n);
                                }
                            } finally {
                                stream.close();
                            }
                            String jsonText = writer.toString();
                            JSONObject jsonObj = new JSONObject(jsonText);
                            importObject(jsonObj);
                        }
                    }
                }
            }
            namesPass.clear();
            Cursor eD = DB.fetchAllPasswords();
            while (eD.moveToNext()) {
                for (eD.moveToFirst(); !eD.isAfterLast(); eD.moveToNext()) {
                    namesPass.add(eD.getString(eD.getColumnIndex(Constants.COLUMN_PIC_SEL)));
                }
            }
            eD.close();
            File sdPathD = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP);
            File[] filesD = sdPathD.listFiles();
            if (filesD != null) {
                int fD = filesD.length;
                if (fD > 0) {
                    for (File file1 : filesD) {
                        String fileName = file1.getName();
                        int pos = fileName.lastIndexOf(".");
                        String fileLoc = sdPathD + "/" + fileName;
                        String fileNameS = fileName.substring(0, pos);
                        if (!namesPass.contains(fileNameS)) {
                            FileInputStream stream = new FileInputStream(fileLoc);
                            Writer writer = new StringWriter();
                            char[] buffer = new char[1024];
                            try {
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(stream, "UTF-8")
                                );
                                int n;
                                while ((n = reader.read(buffer)) != -1) {
                                    writer.write(buffer, 0, n);
                                }
                            } finally {
                                stream.close();
                            }
                            String jsonText = writer.toString();
                            JSONObject jsonObj = new JSONObject(jsonText);
                            importObject(jsonObj);
                        }
                    }
                }
            }
            namesPass.clear();
            Cursor eG = DB.fetchAllPasswords();
            while (eG.moveToNext()) {
                for (eG.moveToFirst(); !eG.isAfterLast(); eG.moveToNext()) {
                    namesPass.add(eG.getString(eG.getColumnIndex(Constants.COLUMN_PIC_SEL)));
                }
            }
            eG.close();
            File sdPathG = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP);
            File[] filesG = sdPathG.listFiles();
            if (filesG != null) {
                int fG = filesG.length;
                if (fG > 0) {
                    for (File file1 : filesG) {
                        String fileName = file1.getName();
                        int pos = fileName.lastIndexOf(".");
                        String fileLoc = filesG + "/" + fileName;
                        String fileNameS = fileName.substring(0, pos);
                        if (!namesPass.contains(fileNameS)) {
                            FileInputStream stream = new FileInputStream(fileLoc);
                            Writer writer = new StringWriter();
                            char[] buffer = new char[1024];
                            try {
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(stream, "UTF-8")
                                );
                                int n;
                                while ((n = reader.read(buffer)) != -1) {
                                    writer.write(buffer, 0, n);
                                }
                            } finally {
                                stream.close();
                            }
                            String jsonText = writer.toString();
                            JSONObject jsonObj = new JSONObject(jsonText);
                            importObject(jsonObj);
                        }
                    }
                }
            }
            DB.close();
        }
    }

    private void importObject(JSONObject jsonObj) throws JSONException {
        String title = null;
        if (!jsonObj.isNull(Constants.COLUMN_TITLE)) {
            title = jsonObj.getString(Constants.COLUMN_TITLE);
        }
        String login = null;
        if (!jsonObj.isNull(Constants.COLUMN_LOGIN)) {
            login = jsonObj.getString(Constants.COLUMN_LOGIN);
        }
        String password = null;
        if (!jsonObj.isNull(Constants.COLUMN_PASSWORD)) {
            password = jsonObj.getString(Constants.COLUMN_PASSWORD);
        }
        String url = null;
        if (!jsonObj.isNull(Constants.COLUMN_URL)) {
            url = jsonObj.getString(Constants.COLUMN_URL);
        }
        String comment = null;
        if (!jsonObj.isNull(Constants.COLUMN_COMMENT)) {
            comment = jsonObj.getString(Constants.COLUMN_COMMENT);
        }
        String date = null;
        if (!jsonObj.isNull(Constants.COLUMN_DATE)) {
            date = jsonObj.getString(Constants.COLUMN_DATE);
        }
        String color = null;
        if (!jsonObj.isNull(Constants.COLUMN_TECHNICAL)) {
            color = jsonObj.getString(Constants.COLUMN_TECHNICAL);
        }
        String uuID = null;
        if (!jsonObj.isNull(Constants.COLUMN_PIC_SEL)) {
            uuID = jsonObj.getString(Constants.COLUMN_PIC_SEL);
        }
        DB = new DataBase(sContext);
        DB.insertPass(title, login, password, url, comment, date, color, uuID);
    }

    public String generateID(){
        return UUID.randomUUID().toString();
    }

    public static boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                return urlc.getResponseCode() == 200;
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection");
                return false;
            }
        }
        return false;
    }
}
