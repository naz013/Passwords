package com.cray.software.passwords.utils

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

open class PrefsConstants {
    companion object {

        val PREFS_NAME = "pass_prefs"

        val RATE_SHOW = "show_rate_state"
        val APP_RUNS_COUNT = "app_runs_state"
        val APP_THEME = "theme_"
        val APP_THEME_COLOR = "theme_color"
        val DELETE_BACKUP = "delete_backup_state"
        val PASSWORD_LENGTH = "edit_length_state"
        val PASSWORD_OLD_LENGTH = "edit_old_length_state"
        val KEYWORD = "restore_key_state"
        val AUTO_BACKUP = "auto_backup_state"
        val AUTO_SYNC = "auto_sunc_state"
        val ORDER_BY = "order_by"

        val LOGIN_PASSCODE = "login_passcode"
        val LOGIN_PREFS = "next_settings"

        val DRIVE_USER = "ggl_user"
        val DROPBOX_UID = "dropbox_uid"
        val DROPBOX_TOKEN = "dropbox_token"
    }
}
