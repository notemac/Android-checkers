package ru.bstu.checkers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    Button b;
    TextView tv;
    ImageButton ib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // tv = findViewById(R.id.ttt);
        //tv.setOnClickListener(this);
        //ib = findViewById(R.id.imageView2);
        //ib.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_exitgame:
                intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            case R.id.actionbar_backmove:
                intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
