package com.cray.software.passwords.helpers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.notes.NoteItem;
import com.cray.software.passwords.passwords.Password;
import com.google.gson.Gson;

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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncHelper {

    private Context mContext;

    public SyncHelper(Context context) {
        this.mContext = context;
    }

    public void exportPasswords() throws JSONException, IOException {
        List<Password> list = DataProvider.getOriginalData(mContext);
        File sdPath = Environment.getExternalStorageDirectory();
        File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
        if (!sdPathDr.exists()) {
            sdPathDr.mkdirs();
        }
        for (Password password : list) {
            if (isSdPresent()) {
                String exportFileName = password.getUuId() + Constants.FILE_EXTENSION;
                File file = new File(sdPathDr, exportFileName);
                if (file.exists()) {
                    file.delete();
                }
                FileWriter fw = new FileWriter(file);
                fw.write(new Gson().toJson(password));
                fw.close();
            } else Log.i("reminder-info", "Couldn't find external storage!");
        }
    }

    public void exportNotes() throws JSONException, IOException {
        List<NoteItem> list = DataProvider.getOriginalNotes(mContext);
        File sdPath = Environment.getExternalStorageDirectory();
        File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
        if (!sdPathDr.exists()) {
            sdPathDr.mkdirs();
        }
        for (NoteItem item : list) {
            if (isSdPresent()) {
                String exportFileName = item.getKey() + Constants.FILE_EXTENSION_NOTE;
                File file = new File(sdPathDr, exportFileName);
                if (file.exists()) {
                    file.delete();
                }
                FileWriter fw = new FileWriter(file);
                fw.write(new Gson().toJson(item));
                fw.close();
            } else Log.i("reminder-info", "Couldn't find external storage!");
        }
    }

    public void importObjectsFromJson() throws IOException, JSONException {
        if (isSdPresent()) {
            List<String> namesPass = new ArrayList<>();
            for (Password item : DataProvider.getOriginalData(mContext)) {
                namesPass.add(item.getUuId());
            }
            for (NoteItem item : DataProvider.getOriginalNotes(mContext)) {
                namesPass.add(item.getKey());
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
                        if (fileName.endsWith(Constants.FILE_EXTENSION_NOTE)) {
                            importNote(writer.toString());
                        } else {
                            importPassword(writer.toString());
                        }
                    }
                }
            }
        }
    }

    private void importNote(String jsonText) throws JSONException {
        DataProvider.saveNote(mContext, new Gson().fromJson(jsonText, NoteItem.class));
    }

    private void importPassword(String jsonText) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonText);
        String title = null;
        if (!jsonObj.isNull(DataBase.COLUMN_TITLE)) {
            title = jsonObj.getString(DataBase.COLUMN_TITLE);
        }
        String login = null;
        if (!jsonObj.isNull(DataBase.COLUMN_LOGIN)) {
            login = jsonObj.getString(DataBase.COLUMN_LOGIN);
        }
        String password = null;
        if (!jsonObj.isNull(DataBase.COLUMN_PASSWORD)) {
            password = jsonObj.getString(DataBase.COLUMN_PASSWORD);
        }
        String url = null;
        if (!jsonObj.isNull(DataBase.COLUMN_URL)) {
            url = jsonObj.getString(DataBase.COLUMN_URL);
        }
        String comment = null;
        if (!jsonObj.isNull(DataBase.COLUMN_COMMENT)) {
            comment = jsonObj.getString(DataBase.COLUMN_COMMENT);
        }
        String date = null;
        if (!jsonObj.isNull(DataBase.COLUMN_DATE)) {
            date = jsonObj.getString(DataBase.COLUMN_DATE);
        }
        int colorPass = 0;
        if (jsonObj.has(DataBase.COLUMN_TECHNICAL)) {
            try {
                String color = jsonObj.getString(DataBase.COLUMN_TECHNICAL);
                if (color != null) {
                    colorPass = Integer.parseInt(color);
                }
            } catch (ClassCastException e) {
                colorPass = jsonObj.getInt(DataBase.COLUMN_TECHNICAL);
            }
        }
        String uuID = null;
        if (!jsonObj.isNull(DataBase.COLUMN_PIC_SEL)) {
            uuID = jsonObj.getString(DataBase.COLUMN_PIC_SEL);
        }
        DataProvider.savePassword(mContext, new Password(title, date, login, comment, url, 0, colorPass, password, uuID));
    }

    public static String generateID() {
        return UUID.randomUUID().toString();
    }

    public static boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
}
