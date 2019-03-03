package com.cray.software.passwords.cloud

import android.content.Context
import android.os.Environment
import android.util.Log

import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.utils.LogUtil
import com.cray.software.passwords.utils.Prefs
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList

import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.ArrayList
import java.util.Collections

/**
 * Copyright 2016 Nazar Suhovich
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class Google @Throws(IllegalStateException::class)
private constructor(context: Context) {

    private var driveService: Drive? = null
    var drive: Drives? = null
        private set

    init {
        val user = Prefs.getInstance(context).driveUser
        if (user.matches(".*@.*".toRegex())) {
            val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE))
            credential.selectedAccountName = user
            val mJsonFactory = GsonFactory.getDefaultInstance()
            val mTransport = AndroidHttp.newCompatibleTransport()
            driveService = Drive.Builder(mTransport, mJsonFactory, credential).setApplicationName(APPLICATION_NAME).build()
            drive = Drives()
        } else {
            logOut()
            throw IllegalArgumentException("Not logged to Google")
        }
    }

    internal fun logOut() {
        Prefs.getInstance().driveUser = Prefs.DRIVE_USER_NONE
        instance = null
    }

    inner class Drives {

        /**
         * Holder application folder identifier on Google Drive.
         *
         * @return Drive folder identifier.
         */
        private val folderId: String?
            @Throws(IOException::class, IllegalArgumentException::class)
            get() {
                val request = driveService!!.files().list()
                        .setQ("mimeType = 'application/vnd.google-apps.folder' and name contains '$FOLDER_NAME'")
                        ?: return null
                do {
                    val files = request.execute() ?: return null
                    val fileList = files.files as ArrayList<File>
                    for (f in fileList) {
                        val fileMIME = f.mimeType
                        if (fileMIME.trim { it <= ' ' }.contains("application/vnd.google-apps.folder") && f.name.contains(FOLDER_NAME)) {
                            LogUtil.d(TAG, "getFolderId: " + f.name + ", " + f.mimeType)
                            return f.id
                        }
                    }
                    request.pageToken = files.nextPageToken
                } while (request.pageToken != null && request.pageToken.length >= 0)
                val file = createFolder()
                return file?.id
            }

        /**
         * Count all backup files stored on Google Drive.
         *
         * @return number of files in local folder.
         */
        @Throws(IOException::class)
        fun countFiles(): Int {
            var count = 0
            val request = driveService!!.files().list().setQ("mimeType = 'text/plain'").setFields("nextPageToken, files")
            do {
                val files = request.execute()
                val fileList = files.files as ArrayList<File>
                for (f in fileList) {
                    val title = f.name
                    if (title.contains(Constants.FILE_EXTENSION)) {
                        count++
                    }
                }
                request.pageToken = files.nextPageToken
            } while (request.pageToken != null && request.pageToken.length >= 0)
            return count
        }

        @Throws(IOException::class)
        private fun removeAllCopies(fileName: String) {
            val request = driveService!!.files().list()
                    .setQ("mimeType = 'text/plain' and name contains '$fileName'")
                    .setFields("nextPageToken, files")
            do {
                val files = request.execute()
                val fileList = files.files as ArrayList<File>
                for (f in fileList) {
                    driveService!!.files().delete(f.id).execute()
                }
                request.pageToken = files.nextPageToken
            } while (request.pageToken != null && request.pageToken.length >= 0)
        }

        /**
         * Upload file from folder to Google Drive.
         *
         * @param metadata metadata.
         * @throws IOException
         */
        @Throws(IOException::class)
        private fun saveFileToDrive(pathToFile: String, metadata: Metadata) {
            if (metadata.folder == null) return
            val files = metadata.folder.listFiles() ?: return
            var folderId: String? = null
            try {
                folderId = folderId
            } catch (ignored: IllegalArgumentException) {
            }

            if (folderId == null) {
                return
            }
            val f = java.io.File(pathToFile)
            if (!f.exists()) {
                return
            }
            if (!f.name.endsWith(metadata.fileExt)) return
            removeAllCopies(f.name)
            val fileMetadata = File()
            fileMetadata.name = f.name
            fileMetadata.description = metadata.meta
            fileMetadata.parents = listOf(folderId)
            val mediaContent = FileContent("text/plain", f)
            driveService!!.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute()
        }

        @Throws(IOException::class)
        fun download(deleteBackup: Boolean, metadata: Metadata) {
            val folder = metadata.folder
            if (!folder.exists() && !folder.mkdirs()) {
                return
            }
            val request = driveService!!.files().list()
                    .setQ("mimeType = 'text/plain' and name contains '" + metadata.fileExt + "'")
                    .setFields("nextPageToken, files")
            do {
                val files = request.execute()
                val fileList = files.files as ArrayList<File>
                for (f in fileList) {
                    val title = f.name
                    if (title.endsWith(metadata.fileExt)) {
                        val file = java.io.File(folder, title)
                        if (!file.exists()) {
                            file.createNewFile()
                        }
                        val out = FileOutputStream(file)
                        driveService!!.files().get(f.id).executeMediaAndDownloadTo(out)
                        if (metadata.action != null) {
                            metadata.action.onSave(file)
                        }
                        if (deleteBackup) {
                            if (file.exists()) {
                                file.delete()
                            }
                            driveService!!.files().delete(f.id).execute()
                        }
                    }
                }
                request.pageToken = files.nextPageToken
            } while (request.pageToken != null && request.pageToken.length >= 0)
        }

        @Throws(IOException::class)
        fun saveFileToDrive() {
            var folderId = folderId
            if (folderId == null) {
                val destFolder = createFolder()
                folderId = destFolder!!.id
            }

            val sdPath = Environment.getExternalStorageDirectory()
            val sdPathDr = java.io.File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD)
            val files = sdPathDr.listFiles()
            for (file in files) {
                val fileMetadata = com.google.api.services.drive.model.File()
                fileMetadata.name = file.name
                fileMetadata.description = "Passwords Backup"
                fileMetadata.parents = listOf<String>(folderId)
                val mediaContent = FileContent("text/plain", file)

                deleteFile(file.name)

                driveService!!.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute()
            }
        }

        @Throws(IOException::class)
        fun loadFileFromDrive() {
            val sdPath = Environment.getExternalStorageDirectory()
            val sdPathDr = java.io.File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP)
            //deleteFolders();
            val request: Drive.Files.List
            try {
                request = driveService!!.files().list().setQ("mimeType = 'text/plain'") // .setQ("mimeType=\"text/plain\"");
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }

            do {
                val files: FileList
                try {
                    files = request.execute()
                } catch (e: IOException) {
                    e.printStackTrace()
                    return
                }

                val fileList = files.files as ArrayList<com.google.api.services.drive.model.File>
                for (f in fileList) {
                    val title = f.name
                    if (!sdPathDr.exists() && !sdPathDr.mkdirs()) {
                        throw IOException("Unable to create parent directory")
                    }

                    if (title.endsWith(Constants.FILE_EXTENSION)) {
                        val file = java.io.File(sdPathDr, title)
                        if (!file.exists()) {
                            try {
                                file.createNewFile() //otherwise dropbox client will fail silently
                            } catch (e1: IOException) {
                                e1.printStackTrace()
                            }

                        }

                        val out = FileOutputStream(file)
                        driveService!!.files().get(f.id).executeMediaAndDownloadTo(out)
                    }
                }
                request.pageToken = files.nextPageToken
            } while (request.pageToken != null && request.pageToken.length >= 0)
        }

        fun deleteFile(title: String) {
            var request: Drive.Files.List? = null
            try {
                request = driveService!!.files().list().setQ("mimeType = 'text/plain'")
            } catch (e: IOException) {
                e.printStackTrace()
            }

            do {
                val files: FileList
                try {
                    files = request!!.execute()
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }

                val fileList = files.files as ArrayList<com.google.api.services.drive.model.File>
                for (f in fileList) {
                    val fileTitle = f.name

                    if (fileTitle.endsWith(Constants.FILE_EXTENSION) && fileTitle.contains(title)) {
                        try {
                            Log.d(Constants.LOG_TAG, "file deleted $fileTitle")
                            driveService!!.files().delete(f.id).execute()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }
                request.pageToken = files.nextPageToken
            } while (request!!.pageToken != null && request.pageToken.length >= 0)
        }

        /**
         * Delete application folder from Google Drive.
         */
        @Throws(IOException::class)
        fun clean() {
            val request = driveService!!.files().list()
                    .setQ("mimeType = 'application/vnd.google-apps.folder' and name contains '$FOLDER_NAME'")
                    ?: return
            do {
                val files = request.execute()
                val fileList = files.files as ArrayList<File>
                for (f in fileList) {
                    val fileMIME = f.mimeType
                    if (fileMIME.contains("application/vnd.google-apps.folder") && f.name.contains(FOLDER_NAME)) {
                        driveService!!.files().delete(f.id).execute()
                        break
                    }
                }
                request.pageToken = files.nextPageToken
            } while (request.pageToken != null && request.pageToken.length >= 0)
        }

        /**
         * Remove all backup files from app folder.
         *
         * @throws IOException
         */
        @Throws(IOException::class)
        fun cleanFolder() {
            val request = driveService!!.files().list()
                    .setQ("mimeType = 'text/plain' and (name contains '" + Constants.FILE_EXTENSION + "')")
                    ?: return
            do {
                val files = request.execute()
                val fileList = files.files as ArrayList<File>
                for (f in fileList) {
                    driveService!!.files().delete(f.id).execute()
                }
                request.pageToken = files.nextPageToken
            } while (request.pageToken != null && request.pageToken.length >= 0)
        }

        /**
         * Create application folder on Google Drive.
         *
         * @return Drive folder
         * @throws IOException
         */
        @Throws(IOException::class)
        private fun createFolder(): File? {
            val folder = File()
            folder.name = FOLDER_NAME
            folder.mimeType = "application/vnd.google-apps.folder"
            val folderInsert = driveService!!.files().create(folder)
            return folderInsert?.execute()
        }

        private inner class Metadata internal constructor(internal val fileExt: String, internal val folder: java.io.File, internal val meta: String, val action: Action)

    }

    private interface Action {
        fun onSave(file: java.io.File)
    }

    companion object {

        private val TAG = "Google"
        private val APPLICATION_NAME = "Passwords/2.0"
        private val FOLDER_NAME = "Passwords"

        private var instance: Google? = null

        fun getInstance(context: Context): Google? {
            try {
                instance = Google(context.applicationContext)
            } catch (e: IllegalArgumentException) {
                LogUtil.d(TAG, "getInstance: " + e.localizedMessage)
            } catch (e: NullPointerException) {
                LogUtil.d(TAG, "getInstance: " + e.localizedMessage)
            }

            return instance
        }
    }
}
