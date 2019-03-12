package com.cray.software.passwords.helpers

import android.content.Context
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.util.*

class SyncHelper(private val mContext: Context) {

    @Throws(JSONException::class, IOException::class)
    fun exportPasswords() {
//        val list = DataProvider.getOriginalData(mContext)
//        val sdPath = Environment.getExternalStorageDirectory()
//        val sdPathDr = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD)
//        if (!sdPathDr.exists()) {
//            sdPathDr.mkdirs()
//        }
//        for (password in list) {
//            if (isSdPresent) {
//                val exportFileName = password.uuId!! + Constants.FILE_EXTENSION
//                val file = File(sdPathDr, exportFileName)
//                if (file.exists()) {
//                    file.delete()
//                }
//                val fw = FileWriter(file)
//                fw.write(Gson().toJson(password))
//                fw.close()
//            } else
//                Log.i("reminder-info", "Couldn't find external storage!")
//        }
    }

    @Throws(JSONException::class, IOException::class)
    fun exportNotes() {
//        if (!isSdPresent) return
//        val list = DataProvider.getOriginalNotes(mContext)
//        val sdPath = Environment.getExternalStorageDirectory()
//        val sdPathDr = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD)
//        if (!sdPathDr.exists()) {
//            sdPathDr.mkdirs()
//        }
//        for (item in list) {
//            val exportFileName = item.key!! + Constants.FILE_EXTENSION_NOTE
//            val file = File(sdPathDr, exportFileName)
//            if (file.exists()) {
//                file.delete()
//            }
//            val fw = FileWriter(file)
//            val json = Gson().toJson(item)
//            fw.write(json)
//            fw.close()
//        }
    }

    @Throws(IOException::class, JSONException::class)
    fun importObjectsFromJson() {
//        if (isSdPresent) {
//            val namesPass = ArrayList<String>()
//            for (item in DataProvider.getOriginalData(mContext)) {
//                namesPass.add(item.uuId)
//            }
//            for (item in DataProvider.getOriginalNotes(mContext)) {
//                namesPass.add(item.key)
//            }
//            val sdPath = Environment.getExternalStorageDirectory()
//            var folder = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD)
//            importFromFolder(folder, namesPass)
//            folder = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP)
//            importFromFolder(folder, namesPass)
//            folder = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP)
//            importFromFolder(folder, namesPass)
//        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun importFromFolder(folder: File, names: MutableList<String>) {
//        if (!folder.exists()) return
//        val files = folder.listFiles() ?: return
//        if (files.size > 0) {
//            for (file1 in files) {
//                val fileName = file1.name
//                val pos = fileName.lastIndexOf(".")
//                val fileLoc = "$folder/$fileName"
//                val fileNameS = fileName.substring(0, pos)
//                if (!names.contains(fileNameS)) {
//                    names.add(fileNameS)
//                    val stream = FileInputStream(fileLoc)
//                    val writer = StringWriter()
//                    val buffer = CharArray(1024)
//                    try {
//                        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
//                        var n: Int
//                        while ((n = reader.read(buffer)) != -1) {
//                            writer.write(buffer, 0, n)
//                        }
//                    } finally {
//                        stream.close()
//                    }
//                    val json = writer.toString()
//                    if (fileName.endsWith(Constants.FILE_EXTENSION_NOTE)) {
//                        importNote(json)
//                    } else {
//                        importPassword(json)
//                    }
//                }
//            }
//        }
    }

    @Throws(JSONException::class)
    private fun importNote(jsonText: String) {
//        val item = Gson().fromJson(jsonText, NoteItem::class.java)
//        DataProvider.saveNote(mContext, item)
    }

    @Throws(JSONException::class)
    private fun importPassword(jsonText: String) {
//        try {
//            DataProvider.savePassword(mContext, Gson().fromJson(jsonText, Password::class.java))
//        } catch (e: Exception) {
//            val jsonObj = JSONObject(jsonText)
//            var title: String? = null
//            if (!jsonObj.isNull(DataBase.COLUMN_TITLE)) {
//                title = jsonObj.getString(DataBase.COLUMN_TITLE)
//            }
//            var login: String? = null
//            if (!jsonObj.isNull(DataBase.COLUMN_LOGIN)) {
//                login = jsonObj.getString(DataBase.COLUMN_LOGIN)
//            }
//            var password: String? = null
//            if (!jsonObj.isNull(DataBase.COLUMN_PASSWORD)) {
//                password = jsonObj.getString(DataBase.COLUMN_PASSWORD)
//            }
//            var url: String? = null
//            if (!jsonObj.isNull(DataBase.COLUMN_URL)) {
//                url = jsonObj.getString(DataBase.COLUMN_URL)
//            }
//            var comment: String? = null
//            if (!jsonObj.isNull(DataBase.COLUMN_COMMENT)) {
//                comment = jsonObj.getString(DataBase.COLUMN_COMMENT)
//            }
//            var date: String? = null
//            if (!jsonObj.isNull(DataBase.COLUMN_DATE)) {
//                date = jsonObj.getString(DataBase.COLUMN_DATE)
//            }
//            var colorPass = 0
//            if (jsonObj.has(DataBase.COLUMN_TECHNICAL)) {
//                try {
//                    val color = jsonObj.getString(DataBase.COLUMN_TECHNICAL)
//                    if (color != null) {
//                        colorPass = Integer.parseInt(color)
//                    }
//                } catch (e1: ClassCastException) {
//                    colorPass = jsonObj.getInt(DataBase.COLUMN_TECHNICAL)
//                }
//
//            }
//            var uuID: String? = null
//            if (!jsonObj.isNull(DataBase.COLUMN_PIC_SEL)) {
//                uuID = jsonObj.getString(DataBase.COLUMN_PIC_SEL)
//            }
//            DataProvider.savePassword(mContext, Password(title, date, login, comment, url, 0, colorPass, password, uuID))
//        }

    }

    companion object {

        private val TAG = "SyncHelper"

        fun generateID(): String {
            return UUID.randomUUID().toString()
        }

        val isSdPresent: Boolean
            get() = android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
    }
}
