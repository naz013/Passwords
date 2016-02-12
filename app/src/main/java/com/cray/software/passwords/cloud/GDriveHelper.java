package com.cray.software.passwords.cloud;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.interfaces.Constants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class GDriveHelper {

    Context ctx;
    SharedPrefs prefs;

    private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory mJsonFactory = GsonFactory.getDefaultInstance();
    private Drive driveService;
    private static final String APPLICATION_NAME = "Passwords/1.3";

    public GDriveHelper(Context context){
        this.ctx = context;
    }

    public void authorize(){
        prefs = new SharedPrefs(ctx);
        GoogleAccountCredential m_credential = GoogleAccountCredential.usingOAuth2(ctx, Collections.singleton(DriveScopes.DRIVE));
        m_credential.setSelectedAccountName(prefs.loadSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER));
        driveService = new Drive.Builder(
                mTransport, mJsonFactory, m_credential).setApplicationName(APPLICATION_NAME)
                .build();
    }

    public boolean isLinked(){
        prefs = new SharedPrefs(ctx);
        return prefs.isSystemKey(Constants.NEW_PREFERENCES_DRIVE_USER) && !prefs.loadSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER).matches(Constants.DRIVE_USER_NONE);
    }

    public void unlink(){
        prefs.saveSystemPrefs(Constants.NEW_PREFERENCES_DRIVE_USER, Constants.DRIVE_USER_NONE);
    }

    public void saveFileToDrive() throws IOException {
        if (isLinked()) {
            authorize();
            String folderId = getFolderId();
            if (folderId == null){
                com.google.api.services.drive.model.File destFolder = createFolder();
                folderId = destFolder.getId();
            }

            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
            File[] files = sdPathDr.listFiles();
            for (File file : files) {
                com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                fileMetadata.setName(file.getName());
                fileMetadata.setDescription("Passwords Backup");
                fileMetadata.setParents(Collections.singletonList(folderId));
                FileContent mediaContent = new FileContent("text/plain", file);

                deleteFile(file.getName());

                driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
            }
        }
    }

    public void loadFileFromDrive() throws IOException {
        if (isLinked()) {
            authorize();
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP);
            //deleteFolders();
            Drive.Files.List request;
            try {
                request = driveService.files().list().setQ("mimeType = 'text/plain'"); // .setQ("mimeType=\"text/plain\"");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            do {
                FileList files;
                try {
                    files = request.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                ArrayList<com.google.api.services.drive.model.File> fileList =
                        (ArrayList<com.google.api.services.drive.model.File>) files.getFiles();
                for (com.google.api.services.drive.model.File f : fileList) {
                    String title = f.getName();
                    if (!sdPathDr.exists() && !sdPathDr.mkdirs()) {
                        throw new IOException("Unable to create parent directory");
                    }

                    if (title.endsWith(Constants.FILE_EXTENSION)) {
                        File file = new File(sdPathDr, title);
                        if (!file.exists()) {
                            try {
                                file.createNewFile(); //otherwise dropbox client will fail silently
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        OutputStream out = new FileOutputStream(file);
                        driveService.files().get(f.getId()).executeMediaAndDownloadTo(out);
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }
    }

    public void deleteFile (String title){
        if (isLinked()) {
            authorize();
            Drive.Files.List request = null;
            try {
                request = driveService.files().list().setQ("mimeType = 'text/plain'");
            } catch (IOException e) {
                e.printStackTrace();
            }
            do {
                FileList files;
                try {
                    files = request.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                ArrayList<com.google.api.services.drive.model.File> fileList =
                        (ArrayList<com.google.api.services.drive.model.File>) files.getFiles();
                for (com.google.api.services.drive.model.File f : fileList) {
                    String fileTitle = f.getName();

                    if (fileTitle.endsWith(Constants.FILE_EXTENSION) && fileTitle.contains(title)) {
                        try {
                            Log.d(Constants.LOG_TAG, "file deleted " + fileTitle);
                            driveService.files().delete(f.getId()).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        }
    }

    private String getFolderId(){
        String id = null;
        Drive.Files.List request = null;
        try {
            request = driveService.files().list().setQ("mimeType = 'application/vnd.google-apps.folder'");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (request != null) {
            do {
                FileList files = null;
                try {
                    files = request.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    id = null;
                }
                if (files != null) {
                    ArrayList<com.google.api.services.drive.model.File> fileList =
                            (ArrayList<com.google.api.services.drive.model.File>) files.getFiles();
                    for (com.google.api.services.drive.model.File f : fileList) {
                        String fileMIME = f.getMimeType();

                        if (fileMIME.trim().matches("application/vnd.google-apps.folder") && f.getName().matches("Passwords")) {
                            id = f.getId();
                        }
                    }
                    request.setPageToken(files.getNextPageToken());
                } else id = null;
            } while (request.getPageToken() != null && request.getPageToken().length() >= 0);
        } else id = null;
        return id;
    }

    private com.google.api.services.drive.model.File createFolder() throws IOException {
        com.google.api.services.drive.model.File folder = new com.google.api.services.drive.model.File();
        folder.setName("Passwords");
        folder.setMimeType("application/vnd.google-apps.folder");
        Drive.Files.Create folderInsert = null;
        try {
            folderInsert = driveService.files().create(folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderInsert != null ? folderInsert.execute() : null;
    }
}
