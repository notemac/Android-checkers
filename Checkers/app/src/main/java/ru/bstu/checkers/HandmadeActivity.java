package ru.bstu.checkers;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HandmadeActivity extends Activity {
    ImageView iv1, iv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        linLayout.setGravity(Gravity.CENTER);
        setContentView(linLayout, layoutParam);

        ImageView iv = new ImageView(this);
        iv1 = iv;
        layoutParam = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.big_draught_size),
                (int)getResources().getDimension(R.dimen.big_draught_size));
        layoutParam.gravity = Gravity.RIGHT;
        iv.setLayoutParams(layoutParam);
        iv.setImageResource(R.drawable.big_black_draught);
        linLayout.addView(iv);

        TextView tv = new TextView(this);
        layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setClickable(true);
        tv.setLayoutParams(layoutParam);
        tv.setText(getString(R.string.text_tv_menu_newgame));
        tv.setBackground(getDrawable(R.drawable.menu_item));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(40);
        tv.setTypeface(null, Typeface.BOLD_ITALIC);
        tv.setTextColor(getColorStateList(R.color.menu_item_color));
        linLayout.addView(tv);

        tv = new TextView(this);
        layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setClickable(true);
        tv.setLayoutParams(layoutParam);
        tv.setText(getString(R.string.text_tv_menu_loadgame));
        tv.setBackground(getDrawable(R.drawable.menu_item));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(40);
        tv.setTypeface(null, Typeface.BOLD_ITALIC);
        tv.setTextColor(getColorStateList(R.color.menu_item_color));
        linLayout.addView(tv);

        tv = new TextView(this);
        layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setClickable(true);
        tv.setLayoutParams(layoutParam);
        tv.setText(getString(R.string.text_tv_menu_settings));
        tv.setBackground(getDrawable(R.drawable.menu_item));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(40);
        tv.setTypeface(null, Typeface.BOLD_ITALIC);
        tv.setTextColor(getColorStateList(R.color.menu_item_color));
        linLayout.addView(tv);

        tv = new TextView(this);
        layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setClickable(true);
        tv.setLayoutParams(layoutParam);
        tv.setText(getString(R.string.text_tv_menu_exitgame));
        tv.setBackground(getDrawable(R.drawable.menu_item));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(40);
        tv.setTypeface(null, Typeface.BOLD_ITALIC);
        tv.setTextColor(getColorStateList(R.color.menu_item_color));
        linLayout.addView(tv);

        iv = new ImageView(this);
        iv2 = iv;
        layoutParam = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.big_draught_size),
                (int)getResources().getDimension(R.dimen.big_draught_size));
        layoutParam.gravity = Gravity.LEFT;
        iv.setLayoutParams(layoutParam);
        iv.setImageResource(R.drawable.big_white_draught);
        linLayout.addView(iv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TranslateAnimation animation = new TranslateAnimation(300.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(2000);
        //animation.setFillAfter(true);
        iv1.startAnimation(animation);
        animation = new TranslateAnimation(-300.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(2000);
        //animation.setFillAfter(true);
        iv2.startAnimation(animation);
    }
}
