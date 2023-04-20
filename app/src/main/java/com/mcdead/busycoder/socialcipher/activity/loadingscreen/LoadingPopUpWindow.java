package com.mcdead.busycoder.socialcipher.activity.loadingscreen;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.mcdead.busycoder.socialcipher.R;

public class LoadingPopUpWindow extends PopupWindow {
    private LoadingPopUpWindow(Activity activity) {
        super(activity);
    }

    public static LoadingPopUpWindow generatePopUpWindow(
            Activity activity,
            LayoutInflater inflater)
    {
        if (inflater == null) return null;

        View popUpView = inflater.inflate(R.layout.loader_screen, null);

        if (popUpView == null) return null;

        LoadingPopUpWindow loadingPopUpWindow = new LoadingPopUpWindow(activity);

        loadingPopUpWindow.setContentView(popUpView);
        // todo: change background;
        //loadingPopUpWindow.setBackgroundDrawable();
        loadingPopUpWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        loadingPopUpWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);

        return loadingPopUpWindow;
    }

    public boolean show(View parentView) {
        if (parentView == null) return false;

        showAtLocation(parentView, Gravity.CENTER, 0, 0);

        return true;
    }
}
