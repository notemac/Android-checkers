package ru.bstu.checkers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.menu_newgame).setOnClickListener(this);
        findViewById(R.id.menu_loadgame).setOnClickListener(this);
        findViewById(R.id.menu_settings).setOnClickListener(this);
        findViewById(R.id.menu_exitapp).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.big_black_draught).
                startAnimation(AnimationUtils.loadAnimation(this, R.anim.mytrans1));
        findViewById(R.id.big_white_draught).
                startAnimation(AnimationUtils.loadAnimation(this, R.anim.mytrans2));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.menu_newgame:
                startActivity(new Intent(this, GameActivity.class));
                break;
            case R.id.menu_loadgame:
                //startActivity(new Intent(this, AnimatedActivity.class));
                startActivity(new Intent(this, LoadGameActivity.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
            case R.id.menu_exitapp:
                finish();
                //android.os.Process.killProcess(android.os.Process.myPid());
                //System.exit(1);
                break;
        }
    }
}
