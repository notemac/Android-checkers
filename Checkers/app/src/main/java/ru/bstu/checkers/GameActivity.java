package ru.bstu.checkers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends Activity implements View.OnClickListener{

    private void ApplySettings()
    {
        Resources resources = this.getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int idBlackDraught = preferences.getInt(resources.getString(R.string.idBlackDraught), R.drawable.black_draught);
        int idWhiteDraught = preferences.getInt(resources.getString(R.string.idWhiteDraught), R.drawable.white_draught);
        int idBlackKing = preferences.getInt(resources.getString(R.string.idBlackKing), R.drawable.black_king);
        int idWhiteKing = preferences.getInt(resources.getString(R.string.idWhiteKing), R.drawable.white_king);
        for(int i = 0; i < GameEngine.ways.length; ++i)
        {
            ArrayList<Item> way = GameEngine.ways[i];
            int way_size = way.size();
            for(int j = 0; j < way_size; ++j) {
                Item item = way.get(j);
                ImageButton ib = findViewById(item.id);
                if (item.type == Item.ITEM_TYPE.black)
                {
                    if(item.isKing) ib.setImageResource(idBlackKing);
                    else ib.setImageResource(idBlackDraught);
                }
                else if (item.type == Item.ITEM_TYPE.white) {
                    if (item.isKing) ib.setImageResource(idWhiteKing);
                    else ib.setImageResource(idWhiteDraught);
                }
            }
        }
        PreferencesActivity.isChangedSettings = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        GameEngine.gameActivity = this;

        for(int i = 0; i < GameEngine.ways.length; ++i)
        {
            ArrayList<Item> way = GameEngine.ways[i];
            int way_size = way.size();
            for(int j = 0; j < way_size; ++j) {
                findViewById(way.get(j).id).setOnClickListener(this);
            }
        }
        ApplySettings();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (PreferencesActivity.isChangedSettings) ApplySettings();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.actionbar_backmove:
                return true;
            case R.id.actionbar_settings:
                intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            case R.id.actionbar_savegame:
                Toast.makeText(this, "Game saved!", Toast.LENGTH_LONG).show();
                return  true;
            case R.id.actionbar_exitgame:
                finish();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Игрок решил сделать ход/побить другой шашкой или кликнул второй раз не туда.
     * id - идентификатор шашки/клетки, куда кликнул игрок.*/
    private void RepickMove(int id)
    {
        GameEngine.PrepareForNextMove(true);
        if (GameEngine.CheckTurn(id)) {
            if (GameEngine.isMove) {
                if (GameEngine.CheckMove()) {
                    GameEngine.HighlightMoves();
                    GameEngine.isNeedSecondClick = true;
                }
            } else if (GameEngine.isJump) {
                if (GameEngine.CheckJump()) {
                    GameEngine.HighlightMoves();
                    GameEngine.isNeedSecondClick = true;
                }
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (GameEngine.isNeedSecondClick)
        {
            if (GameEngine.isMove) // Надо ходить
            {
                if (GameEngine.Move(id))
                {
                    GameEngine.PrepareForNextMove(false);
                    GameEngine.NextTurn();
                    GameEngine.SearchForAllMoves();// Игрок сделал ход, теперь ищем ходы для другого игрока
                }
                else {
                    RepickMove(id);
                }
            }
            else if (GameEngine.isJump)
            {
                if (GameEngine.Jump(id))
                {
                    GameEngine.PrepareForNextMove(false);
                    if (!GameEngine.SearchForNextJump())
                    {
                        GameEngine.NextTurn();
                        GameEngine.SearchForAllMoves();
                    }
                }
                else {
                    RepickMove(id);
                }
            }
        }
        else if (GameEngine.CheckTurn(id))
        {
            if (GameEngine.isMove)
            {
                if (GameEngine.CheckMove())
                {
                    GameEngine.HighlightMoves();
                    GameEngine.isNeedSecondClick = true;
                }
            }
            else if (GameEngine.isJump)
            {
                if (GameEngine.CheckJump())
                {
                    GameEngine.HighlightMoves();
                    GameEngine.isNeedSecondClick = true;
                }
            }
            else // Ходов нет
            {
                Toast.makeText(this, "Player " +
                        ((GameEngine.turn == Item.ITEM_TYPE.white) ? "1" : "2") + " is WINNER!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
