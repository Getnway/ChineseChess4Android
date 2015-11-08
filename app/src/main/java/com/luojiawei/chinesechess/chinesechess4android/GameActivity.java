package com.luojiawei.chinesechess.chinesechess4android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class GameActivity extends Activity implements View.OnClickListener {

    final static int BLUETOOTH = 0;
    final static int LOW_RANK = 1;
    final static int MIDDLE_RANK = 2;
    final static int HIGH_RANK = 3;

    String TAG = "GameActivity";
    TextView mTxtOppositeName;
    Button mBtnNewGame;
    Button mBtnFlipBoard;
    Button mBtnUndo;
    GameView gameView;
    final static int REQUEST_SETTING = 100;
    private Intent intentSetting;
    int level = 1;

    public static void actionStart(Context context, int level){
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("level", level);
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

        mTxtOppositeName = (TextView)findViewById(R.id.txt_opposite_name);
        mTxtOppositeName.setVisibility(View.INVISIBLE);
        mBtnNewGame = (Button)findViewById(R.id.btn_new_game);
        mBtnNewGame.setOnClickListener(this);
        mBtnFlipBoard = (Button)findViewById(R.id.btn_flip_board);
        mBtnFlipBoard.setOnClickListener(this);
        mBtnUndo = (Button)findViewById(R.id.btn_undo);
        mBtnUndo.setOnClickListener(this);

        gameView = (GameView)findViewById(R.id.game_view);

        intentSetting = new Intent(this, Setting.class);

        Intent intent = getIntent();
        level = intent.getIntExtra("level", LOW_RANK);
        LogUtil.d(TAG, "" + level);
        switch (level){
            case BLUETOOTH: //蓝牙对战
                mTxtOppositeName.setVisibility(View.VISIBLE);
                gameView.AI = false;
                break;
            case LOW_RANK: //初级
                gameView.AI = true;
                Engine.MIN_LEVEL = 1;
                Engine.LIMIT_TIME = 100;
                break;
            case MIDDLE_RANK: //中级
                gameView.AI = true;
                Engine.MIN_LEVEL = 3;
                Engine.LIMIT_TIME = 1000;
                break;
            case HIGH_RANK: //高级
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
                LogUtil.i(TAG, "newGameClick");
                gameView.newGame();
                if(level == BLUETOOTH){
                    intentSetting.putExtra("AI", false);
                    LogUtil.d(TAG, "put false");
                }else{
                    intentSetting.putExtra("AI", true);
                    LogUtil.d(TAG, "put true");
                }
                startActivityForResult(intentSetting, REQUEST_SETTING);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case REQUEST_SETTING:
                int color = data.getIntExtra(Setting.MY_COLOT, Setting.RED_COLOT);
                int side = data.getIntExtra(Setting.MY_SIDE, Setting.OFFENSIVE);
                int level = data.getIntExtra(Setting.AI_LEVEL, Setting.LOW_RANK);
                LogUtil.d(TAG, "" + color + side + level);
        }

    }
}
