package com.cray.software.passwords.utils;

import android.app.Activity;
import android.app.AlertDialog;

import com.cray.software.passwords.R;

/**
 * Copyright 2017 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Dialogues {

    public static void showRateDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.rate_question);
        builder.setPositiveButton(R.string.button_rate, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Prefs.getInstance(activity).setRateShowed(true);
            SuperUtil.launchMarket(activity);
        });
        builder.setNegativeButton(R.string.button_never, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Prefs.getInstance(activity).setRateShowed(true);
        });
        builder.setNeutralButton(R.string.button_later, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Prefs.getInstance(activity).setRateShowed(false);
            Prefs.getInstance(activity).setRunsCount(0);
        });
        builder.setCancelable(false);
        builder.create().show();
    }
}
