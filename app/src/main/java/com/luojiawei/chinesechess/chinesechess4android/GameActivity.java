package com.luojiawei.chinesechess.chinesechess4android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class GameActivity extends Activity implements View.OnClickListener {
    String TAG = "GameActivity";
    Button mBtnNewGame;
    Button mBtnFlipBoard;
    Button mBtnUndo;
    GameView gameView;

    public static void actionStart(Context context, int rank){
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("rank", rank);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题，全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_game);

        mBtnNewGame = (Button)findViewById(R.id.btn_new_game);
        mBtnNewGame.setOnClickListener(this);
        mBtnFlipBoard = (Button)findViewById(R.id.btn_flip_board);
        mBtnFlipBoard.setOnClickListener(this);
        mBtnUndo = (Button)findViewById(R.id.btn_undo);
        mBtnUndo.setOnClickListener(this);

        gameView = (GameView)findViewById(R.id.game_view);

        Intent intent = getIntent();
        int rank = intent.getIntExtra("rank", 10);
        switch (rank){
            case 0: //蓝牙对战
                gameView.AI = false;
                break;
            case 1: //初级
                gameView.AI = true;
                Engine.MIN_LEVEL = 1;
                Engine.LIMIT_TIME = 100;
                break;
            case 2: //中级
                gameView.AI = true;
                Engine.MIN_LEVEL = 3;
                Engine.LIMIT_TIME = 1000;
                break;
            case 3: //高级
                gameView.AI = true;
                Engine.MIN_LEVEL = 3;
                Engine.LIMIT_TIME = 5000;
                break;
            default:
                gameView.AI = true;
                Engine.MIN_LEVEL = 1;
                Engine.LIMIT_TIME = 100;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_new_game:
                LogUtil.i(TAG,"newGameClick");
                gameView.newGame();
                break;
            case R.id.btn_flip_board:
                LogUtil.i(TAG,"flipBoardClick");
                gameView.flipBoard();
                break;
            case R.id.btn_undo:
                LogUtil.i(TAG,"undoClick");
                gameView.undo();
            default:
                break;
        }
    }

}
