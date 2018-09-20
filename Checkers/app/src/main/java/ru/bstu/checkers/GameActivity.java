package ru.bstu.checkers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        for(int i = 0; i < GameEngine.ways.length; ++i)
        {
            ArrayList<Item> way = GameEngine.ways[i];
            int way_size = way.size();
            for(int j = 0; j < way_size; ++j)
                findViewById(way.get(j).id).setOnClickListener(this);
        }
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
                return  true;
            case R.id.actionbar_savegame:
                return  true;
            case R.id.actionbar_exitgame:
                finish();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (GameEngine.isNeedSecondClick)
        {
            if (GameEngine.isJump)
            {
                if (GameEngine.Jump(id, getWindow()))
                {
                    GameEngine.NextTurn();
                    // Если надо бить еще, бьем дальше. Иначе ищутся ходы для другого игрока
                    GameEngine.SearchForAllMoves();
                }
            }
            else // Надо ходить
            {
                if (GameEngine.Move(id, getWindow()))
                {
                    GameEngine.NextTurn();
                    GameEngine.SearchForAllMoves();// Игрок сделал ход, теперь ищем ходы для другого игрока
                }
            }
        }
        else if (GameEngine.CheckTurn(id))
        {
            if (GameEngine.isJump)
            {
                if (GameEngine.CheckJump())
                {
                    GameEngine.isNeedSecondClick = true;
                }
                //else: Бить обязательно, но выбранная шашки не может бить,
                //поэтому ничего не делаем
            }
            else if (GameEngine.isMove)
            {
                if (GameEngine.CheckMove())
                {
                    GameEngine.isNeedSecondClick = true;
                }
                //else: Ходы имеются, но выбранная шашка не может ходить,
                //поэтому ничего не делаем
            }
            else // Ходов нет
            {
                Toast.makeText(this, "Player " +
                        ((GameEngine.turn == Item.ITEM_TYPE.white) ? "1" : "2") + " is WINNER!",
                        Toast.LENGTH_LONG);
            }
        }
    }
}
