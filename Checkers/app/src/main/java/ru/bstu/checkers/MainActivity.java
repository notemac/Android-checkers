package ru.bstu.checkers;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.a8);
        Drawable drawable = icons.getDrawable(0);*/


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
                startActivity(new Intent(this, AnimatedActivity.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
            case R.id.menu_exitapp:
                finish();
                //android.os.Process.killProcess(android.os.Process.myPid());
                //System.exit(1);
                break;
            /*case R.id.ttt:
                if (tv.isClickable())
                    tv.setClickable(false);
                Toast.makeText(this, "!!!!!", Toast.LENGTH_LONG).show();*/
                //else  tv.setClickable(true);
                //ib.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP); СОЗДАЕТ РАМКУ ВОКРУГ КАРТИНКИ
                //ib.setColorFIlter(null) = undo = ib.ClearColorFilter
                //tv.setSelected(true);
                //tv.setPressed(true);
                //tv.setBackgroundTintMode();
               // tv.setTextColor(getResources().getColor(android.R.color.white, null));
                //.setImageResource(R.drawable.c116);
               // break;
        }
    }
}
