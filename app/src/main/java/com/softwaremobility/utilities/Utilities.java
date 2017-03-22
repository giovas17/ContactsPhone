package com.softwaremobility.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by darkgeat on 3/15/17.
 */

public class Utilities {

    public static void hideKeyboard(Activity context){
        View view = context.getCurrentFocus();
        if (view != null){
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
