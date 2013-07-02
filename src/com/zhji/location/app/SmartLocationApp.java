
package com.zhji.location.app;

import android.app.Application;
import android.content.Context;

public class SmartLocationApp extends Application {
    private static volatile Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setAppContext(this);
    }

    private void setAppContext(final Context context) {
        sContext = context;
    }
}
