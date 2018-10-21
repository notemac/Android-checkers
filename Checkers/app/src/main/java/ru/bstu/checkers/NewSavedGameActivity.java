package ru.bstu.checkers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class NewSavedGameActivity extends Activity {
    public static final String EXTRA_REPLY = "ru.bstu.checkers.gamelistsql.REPLY";
    private EditText mEditGameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_saved_game);

        mEditGameView = findViewById(R.id.edit_gameName);

        final TextView button = findViewById(R.id.button_save_game);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                String gameName = TextUtils.isEmpty(mEditGameView.getText())
                    ? "unnamed" : mEditGameView.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, gameName);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }
    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }
}
