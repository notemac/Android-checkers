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

import java.util.ArrayList;

import ru.bstu.checkers.roomdb.Game;
import ru.bstu.checkers.roomdb.GameViewModel;

public class GameActivity extends Activity
        implements View.OnClickListener, ExitGameDialogFragment.NoticeDialogListener,
                GameOverDialogFragment.NoticeDialogListener{

    public static final int NEW_SAVED_GAME_ACTIVITY_REQUEST_CODE = 1;

    String draughtsSet;
    GameEngine gameEngine;

    private GameViewModel mGameViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameEngine = new GameEngine();
        if (getIntent().getBooleanExtra(LoadGameActivity.EXTRA_LOAD_GAME, false))
        {
            gameEngine.Init(getIntent().getIntExtra(LoadGameActivity.EXTRA_TURN, 0),
                    (ArrayList<Item>[])getIntent().getSerializableExtra(LoadGameActivity.EXTRA_WAYS));
        }
        else {
            gameEngine.Init();
        }
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

        mGameViewModel = new GameViewModel(MyApplication.getInstance());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Resources resources = this.getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String updatedDraughtsSet = preferences.getString(resources.getString(R.string.selectedDraughtsSet),
                resources.getString(R.string.defaultDraughtsSet));
        if (!updatedDraughtsSet.equals(draughtsSet))
        {
            draughtsSet = updatedDraughtsSet;
            gameEngine.LoadDraughtsSet();
            gameEngine.UpdateDraughtsSetForBackMove();
        }
        // TODO: 9/26/2018 сделать вызов PreferencesActivity через startActivityForResult и в OnActivityResult вызывать LoadDraughtsSet
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
                if (gameEngine.BackMoveExist())
                {
                    gameEngine.PrepareForBackMove();
                    gameEngine.BackMove();
                    gameEngine.SearchForAllMoves();
                }
                return true;
            case R.id.actionbar_settings:
                intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            case R.id.actionbar_savegame:
                intent = new Intent(this, NewSavedGameActivity.class);
                startActivityForResult(intent, NEW_SAVED_GAME_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.actionbar_exitgame:
                ExitGameDialogFragment dialog = new ExitGameDialogFragment();
                dialog.show(getFragmentManager(), getResources().getString(R.string.exitDialog));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_SAVED_GAME_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            //СОХРАНЯЕМ ИГРУ
            Game game = new Game(data.getStringExtra(NewSavedGameActivity.EXTRA_REPLY), gameEngine.turn.ordinal());
            ArrayList<ru.bstu.checkers.roomdb.Item>[] ways = new ArrayList[Item.WAYS_COUNT];
            for (int i = 0; i < ways.length; ++i)
                ways[i] = new ArrayList<ru.bstu.checkers.roomdb.Item>(8);// Максимум 8 клеток/шашек на одной диагонали
            for (int i = 0; i < gameEngine.ways.length; ++i)
            {
                ArrayList<Item> items = gameEngine.ways[i];
                for (int j = 0; j < items.size(); ++j)
                {
                    Item item = items.get(j);
                    ArrayList<Integer> idx = item.GetWaysIdx();
                    ways[i].add(new ru.bstu.checkers.roomdb.Item(item.id, item.type.ordinal(), item.isKing, idx));
                }
            }
            mGameViewModel.insert(game, ways);
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
                    gameEngine.SearchForAllMoves();
                }
                else {
                    gameEngine.RepickMove(id);
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
                   gameEngine.RepickMove(id);
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
        }
        if (gameEngine.GameOver())
        {
            String winner;
            if (gameEngine.turn == Item.ITEM_TYPE.white)
                winner = "Player 2 is WINNER!";
            else
                winner = "Player 1 is WINNER!";
            GameOverDialogFragment dialog = new GameOverDialogFragment();
            Bundle args = new Bundle();
            args.putString(getResources().getString(R.string.winner), winner);
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), getResources().getString(R.string.gameOverDialog));
        }
    }

    @Override
    public void onBackPressed() { }

    @Override
    public void onExitGameDialogPositiveClick(DialogFragment dialog) { finish(); }

    @Override
    public void onExitGameDialogNegativeClick(DialogFragment dialog) { }

    @Override
    public void onGameOverDialogClick(DialogFragment dialog) { finish(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}