package com.diveboard.model;

import android.view.View;

public class Header {
    public final String title;
    public final String buttonTitle;
    public final Runnable buttonAction;

    public Header(String title, String buttonTitle, Runnable buttonAction) {
        this.title = title;
        this.buttonTitle = buttonTitle;
        this.buttonAction = buttonAction;
    }

    public void onClick(View view) {
        if (buttonAction != null) {
            buttonAction.run();
        }
    }
}
