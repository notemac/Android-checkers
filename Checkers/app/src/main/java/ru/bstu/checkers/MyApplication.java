package ru.bstu.checkers;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public final class MyApplication extends Application {
    private static MyApplication instance;
    private static Activity currentActivity;
    public static MyApplication getInstance() {
        return instance;
    }
    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    private static final class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks
    {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            currentActivity = activity;
        }
        @Override
        public void onActivityStarted(Activity activity) {
            currentActivity = activity;
        }
        @Override
        public void onActivityResumed(Activity activity) {
            currentActivity = activity;
        }
        @Override
        public void onActivityPaused(Activity activity) {
            currentActivity = null;
        }
        @Override
        public void onActivityStopped(Activity activity) {
            // don't clear current activity because activity may get stopped after
            // the new activity is resumed
        }
        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }
        @Override
        public void onActivityDestroyed(Activity activity) {
            // don't clear current activity because activity may get destroyed after
            // the new activity is resumed
        }
    }
}
