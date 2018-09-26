package ru.bstu.checkers;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends Activity
        implements View.OnClickListener, ExitGameDialogFragment.NoticeDialogListener {

    String draughtsSet;
    GameEngine gameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameEngine = new GameEngine();
        gameEngine.Init(this);
        gameEngine.SearchForAllMoves();

        for(int i = 0; i < gameEngine.ways.length; ++i)
        {
            ArrayList<Item> way = gameEngine.ways[i];
            int way_size = way.size();
            for(int j = 0; j < way_size; ++j) {
                findViewById(way.get(j).id).setOnClickListener(this);
            }
        }

        Resources resources = this.getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        draughtsSet = preferences.getString(resources.getString(R.string.selectedDraughtsSet),
                resources.getString(R.string.defaultDraughtsSet));
        gameEngine.LoadDraughtsSet();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Resources resources = this.getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String updatedDraughtsSet = preferences.getString(resources.getString(R.string.selectedDraughtsSet),
                resources.getString(R.string.defaultDraughtsSet));
        if (!updatedDraughtsSet.equals(draughtsSet))
        {
            draughtsSet = updatedDraughtsSet;
            gameEngine.LoadDraughtsSet();
        }
        // TODO: 9/26/2018 сделать вызов PreferencesActivity через startActivityForResult и в OnActivityResult вызывать LoadDraughtsSet

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
                ExitGameDialogFragment dialog = new ExitGameDialogFragment();
                dialog.show(getFragmentManager(), "EGDF");
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /** Игрок решил сделать ход/побить другой шашкой или кликнул второй раз не туда.
     * id - идентификатор шашки/клетки, куда кликнул игрок.*/
    private void RepickMove(int id)
    {
       gameEngine.PrepareForNextMove(true);
        if (gameEngine.CheckTurn(id)) {
            if (gameEngine.isMove) {
                if (gameEngine.CheckMove()) {
                    gameEngine.HighlightMoves();
                    gameEngine.isNeedSecondClick = true;
                }
            } else if (gameEngine.isJump) {
                if (gameEngine.CheckJump()) {
                    gameEngine.HighlightMoves();
                    gameEngine.isNeedSecondClick = true;
                }
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (gameEngine.isNeedSecondClick)
        {
            if (gameEngine.isMove) // Надо ходить
            {
                if (gameEngine.Move(id))
                {
                    gameEngine.PrepareForNextMove(false);
                    gameEngine.NextTurn();
                    gameEngine.SearchForAllMoves();// Игрок сделал ход, теперь ищем ходы для другого игрока
                }
                else {
                    RepickMove(id);
                }
            }
            else if (gameEngine.isJump)
            {
                if (gameEngine.Jump(id))
                {
                    gameEngine.PrepareForNextMove(false);
                    if (!gameEngine.SearchForNextJump())
                    {
                        gameEngine.NextTurn();
                        gameEngine.SearchForAllMoves();
                    }
                }
                else {
                    RepickMove(id);
                }
            }
        }
        else if (gameEngine.CheckTurn(id))
        {
            if (gameEngine.isMove)
            {
                if (gameEngine.CheckMove())
                {
                    gameEngine.HighlightMoves();
                    gameEngine.isNeedSecondClick = true;
                }
            }
            else if (gameEngine.isJump)
            {
                if (gameEngine.CheckJump())
                {
                    gameEngine.HighlightMoves();
                    gameEngine.isNeedSecondClick = true;
                }
            }
            else // Ходов нет
            {
                Toast.makeText(this, "Player " +
                        ((gameEngine.turn == Item.ITEM_TYPE.white) ? "1" : "2") + " is WINNER!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() { }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameEngine.ExitGame();
    }
}