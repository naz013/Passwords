package com.cray.software.passwords.helpers;

import android.content.Context;
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

    private Context mContext;

    public SyncHelper(Context context){
        this.mContext = context;
    }

    public void exportPasswords() throws JSONException, IOException {
        List<Password> list = DataProvider.getData(mContext);
        for (Password password : list) {
            String uuID = password.getUuId();
            if (uuID == null) {
                uuID = generateID();
            }
            JSONObject jObjectData = new JSONObject();
            jObjectData.put(Constants.COLUMN_TITLE, password.getTitle());
            jObjectData.put(Constants.COLUMN_LOGIN, password.getLogin());
            jObjectData.put(Constants.COLUMN_PASSWORD, password.getPassword());
            jObjectData.put(Constants.COLUMN_URL, password.getUrl());
            jObjectData.put(Constants.COLUMN_COMMENT, password.getComment());
            jObjectData.put(Constants.COLUMN_DATE, password.getDate());
            jObjectData.put(Constants.COLUMN_TECHNICAL, password.getColor());
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
        }
    }

    public void importPasswordFromJSON() throws IOException, JSONException {
        if (isSdPresent()){
            List<String> namesPass = new ArrayList<>();
            for (Password item : DataProvider.getData(mContext)) {
                namesPass.add(item.getUuId());
            }
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
            importFromFolder(sdPathDr, namesPass);
            File sdPathD = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP);
            importFromFolder(sdPathD, namesPass);
            File sdPathG = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP);
            importFromFolder(sdPathG, namesPass);
        }
    }

    private void importFromFolder(File folder, List<String> names) throws IOException, JSONException {
        File[] files = folder.listFiles();
        if (files != null) {
            int f = files.length;
            if (f > 0) {
                for (File file1 : files) {
                    String fileName = file1.getName();
                    int pos = fileName.lastIndexOf(".");
                    String fileLoc = folder + "/" + fileName;
                    String fileNameS = fileName.substring(0, pos);
                    if (!names.contains(fileNameS)) {
                        names.add(fileNameS);
                        FileInputStream stream = new FileInputStream(fileLoc);
                        Writer writer = new StringWriter();
                        char[] buffer = new char[1024];
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
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
        int colorPass = 0;
        if (jsonObj.has(Constants.COLUMN_TECHNICAL)) {
            try {
                String color = jsonObj.getString(Constants.COLUMN_TECHNICAL);
                if (color != null) {
                    colorPass = Integer.parseInt(color);
                }
            } catch (ClassCastException e) {
                colorPass = jsonObj.getInt(Constants.COLUMN_TECHNICAL);
            }
        }
        String uuID = null;
        if (!jsonObj.isNull(Constants.COLUMN_PIC_SEL)) {
            uuID = jsonObj.getString(Constants.COLUMN_PIC_SEL);
        }
        DataProvider.savePassword(mContext, new Password(title, date, login, comment, url, 0, colorPass, password, uuID));
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
