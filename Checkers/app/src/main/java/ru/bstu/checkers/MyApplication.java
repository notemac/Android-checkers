package ru.bstu.checkers;

import android.app.Application;

public final class MyApplication extends Application {
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        GameEngine.Init();
        GameEngine.SearchForMoves();
    }
}
