package com.luojiawei.chinesechess.chinesechess4android;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements View.OnClickListener {
    String TAG = "GameActivity";

    //模式等级
    public static final int BLUETOOTH = 0;
    public static final int LOW_RANK = 1;
    public static final int MIDDLE_RANK = 2;
    public static final int HIGH_RANK = 3;

    //Request Code
    private static final int REQUEST_SETTING = 100;
    private static final int REQUEST_CONNECT_DEVICE = 101;
    private static final int REQUEST_ENABLE_BLUETOOTH = 102;

    //View
    private TextView mTxtOppositeName;
    private Button mBtnNewGame;
    private Button mBtnFlipBoard;
    private Button mBtnUndo;
    private GameView gameView;      //棋盘View

    //与蓝牙相关的变量
    private String mConnectedDeviceName = null;         //已连接设备
    private StringBuffer mOutStringBuffer;              //接收到的数据
    private BluetoothAdapter mBluetoothAdapter = null;  //蓝牙适配器
    private BluetoothService mBluetoothService = null;  //蓝牙服务
    private boolean isBtClient;                         //是否作为蓝牙客户端

    private StringBuffer gameInfo;                      //蓝牙传输信息
    private final String TYPE_NEW = "NEW";              //蓝牙信息类型，新局
    private final String TYPE_MOVE = "MOVE";            //蓝牙信息类型，着法

    private Intent intentSetting;       //设置意图
    private int level;                  //模式等级
    private static boolean isFromMain;  //是否从主界面进入游戏
    private static boolean isGameing;   //是否正在游戏中
    private boolean isDestroyed = false;

    public static void actionStart(Context context, int level) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("level", level);
        isFromMain = true;
        isGameing = false;
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

        //查找View
        mTxtOppositeName = (TextView) findViewById(R.id.txt_opposite_name);
        mTxtOppositeName.setVisibility(View.INVISIBLE);
        mBtnNewGame = (Button) findViewById(R.id.btn_new_game);
        mBtnNewGame.setOnClickListener(this);
        mBtnFlipBoard = (Button) findViewById(R.id.btn_flip_board);
        mBtnFlipBoard.setOnClickListener(this);
        mBtnUndo = (Button) findViewById(R.id.btn_undo);
        mBtnUndo.setOnClickListener(this);
        gameView = (GameView) findViewById(R.id.game_view);
        gameView.setSendMessage(new CommunicationHelper());

        //设置意图
        intentSetting = new Intent(this, Setting.class);

        //获取模式等级，初始化游戏参数
        Intent intent = getIntent();
        level = intent.getIntExtra("level", LOW_RANK);
        LogUtil.d(TAG, "" + level);
        if (level == BLUETOOTH && !isSupportBluetooth()) {
            finish();
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isGameing) {
            if (level == BLUETOOTH) {   //蓝牙模式
                if (!mBluetoothAdapter.isEnabled()) { //蓝牙未打开
                    //请求打开蓝牙
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                } else if (mBluetoothService == null) {
                    mBluetoothService = new BluetoothService(this, mHandler);
                    mOutStringBuffer = new StringBuffer("");
                }
                ensureDiscoverable();
            }
            startSettingActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (level == BLUETOOTH) {
            //如果蓝牙服务已停止，则启动服务
            if (mBluetoothService != null &&
                    mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                LogUtil.d(TAG, "start BT Service");
                mBluetoothService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        if (level == BLUETOOTH) {
            if (mBluetoothService != null) {
                mBluetoothService.stop();   //停止蓝牙服务
            }
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
        switch (v.getId()) {
            case R.id.btn_new_game:     //新游戏
                LogUtil.i(TAG, "newGameClick");
                startSettingActivity();
                break;
            case R.id.btn_flip_board:   //翻转
                LogUtil.i(TAG, "flipBoardClick");
                gameView.flipBoard();
                break;
            case R.id.btn_undo:         //悔棋
                LogUtil.i(TAG, "undoClick");
                gameView.undo();
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (isFromMain) {
                isDestroyed = true;
                finish();
            }
            return;
        }

        switch (requestCode) {
            case REQUEST_SETTING:
                boolean isRed = data.getBooleanExtra(Setting.IS_RED_COLOR, true);
                boolean isOffensive = data.getBooleanExtra(Setting.IS_OFFENSIVE, true);
                int level = data.getIntExtra(Setting.LEVEL, Setting.LOW_RANK);
                isBtClient = data.getBooleanExtra(Setting.IS_BT_CLIENT, false);
                LogUtil.d(TAG, "" + level);
                newGame(isRed, isOffensive, level);
                startDeviceList(isBtClient);
                break;

            case REQUEST_CONNECT_DEVICE:
                connectDevice(data);
                break;

            case REQUEST_ENABLE_BLUETOOTH:
                mBluetoothService = new BluetoothService(this, mHandler);
                mOutStringBuffer = new StringBuffer("");
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
//            isExit.setTitle("提示");
            // 设置对话框消息
            isExit.setMessage(getString(R.string.sure_to_quit));
            // 添加选择按钮并注册监听
            isExit.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_quit), listener);
            isExit.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.btn_cancel), listener);
            // 显示对话框
            isExit.show();
        }
        return false;
    }

    //退出监听器
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    isDestroyed = true;
                    GameActivity.this.finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    /**
     * 开始新游戏
     *
     * @param isRed       是否我方红色
     * @param isOffensive 是否我方先手
     * @param level       电脑等级
     */
    private void newGame(boolean isRed, boolean isOffensive, int level) {
        isGameing = true;
        isFromMain = false;
        LogUtil.d(TAG, "isRed: " + isRed + " isOffensive: " + isOffensive + " level: " + level);
        switch (level) {
            case BLUETOOTH: //蓝牙对战
                mTxtOppositeName.setVisibility(View.VISIBLE);
                gameView.AI = false;
                mBtnUndo.setEnabled(false); //蓝牙对战，悔棋不可用
                gameInfo = new StringBuffer(TYPE_NEW);  //待传输信息
                gameInfo.append("-");
                gameInfo.append(!isRed);
                gameInfo.append("-");
                gameInfo.append(!isOffensive);
                gameInfo.append("-");
                break;
            case LOW_RANK:      //初级
                gameView.AI = true;     //是否人机
                Engine.MIN_LEVEL = 1;   //最低搜索层次
                Engine.LIMIT_TIME = 30; //限制时间（ms）
                break;
            case MIDDLE_RANK:   //中级
                gameView.AI = true;
                Engine.MIN_LEVEL = 3;
                Engine.LIMIT_TIME = 800;
                break;
            case HIGH_RANK:     //高级
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
        gameView.newGame(isRed, isOffensive);
    }

    /**
     * 启动设置
     */
    private void startSettingActivity() {
        if (level == BLUETOOTH) {
            intentSetting.putExtra("AI", false);
            LogUtil.d(TAG, "put false");
        } else {
            intentSetting.putExtra("level", level);
            intentSetting.putExtra("AI", true);
            LogUtil.d(TAG, "put true");
        }
        startActivityForResult(intentSetting, REQUEST_SETTING);
    }

    /**
     * 获取蓝牙适配器，并检测是否支持蓝牙
     *
     * @return 蓝牙可用则返回true，否则false
     */
    private boolean isSupportBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), R.string.bluetooth_disable, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * 打开搜索设备Activity
     */
    private void startDeviceList(boolean isBtClient) {
        if (isBtClient) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        } else {
            mTxtOppositeName.setText(R.string.wait);
        }
    }

    /**
     * 使蓝牙可见
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * 连接其他设备
     *
     * @param data 被连接设备信息
     */
    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBluetoothService.connect(device, true);
    }

    /**
     * 发送信息
     * @param message 信息内容
     */
    private void sendMessageByBT(String message) {
        // 发送消息前先确认蓝牙已连接
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        //有消息传送
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mBluetoothService.write(send);
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * 解读消息
     * @param message 消息内容
     */
    private void readMessage(String message){
        LogUtil.d(TAG, "readMessage: " + message);
        String[] msgs = message.split("-");
        switch (msgs[0]){
            case TYPE_NEW:
                LogUtil.d(TAG,"TYPE_NEW");
                gameView.newGame(Boolean.valueOf(msgs[1]), Boolean.valueOf(msgs[2]));
                break;
            case TYPE_MOVE:
                LogUtil.d(TAG,"TYPE_MOVE");
                gameView.oppMove(Integer.valueOf(msgs[1]));
                break;
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            LogUtil.d(TAG, "STATE_CONNECTED");
                            sendMessageByBT(Build.MODEL);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            LogUtil.d(TAG, "STATE_CONNECTING");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            LogUtil.d(TAG, "STATE_NONE");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_WRITE:
                    LogUtil.d(TAG, "MESSAGE_WRITE");
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMsg = new String(writeBuf);
                    LogUtil.d(TAG, "send: " + writeMsg);
                    break;
                case BluetoothService.MESSAGE_READ:
                    LogUtil.d(TAG, "MESSAGE_READ");
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMsg = new String(readBuf, 0, msg.arg1);
                    LogUtil.d(TAG, "receive: " + readMsg);
                    readMessage(readMsg);
                    break;
                case BluetoothService.MESSAGE_DEVICE_NAME:
                    LogUtil.d(TAG, "MESSAGE_DEVICE_NAME");
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
                    Toast.makeText(getApplication(), getString(R.string.connected_to)
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    mTxtOppositeName.setText(getString(R.string.opponent) + mConnectedDeviceName);
                    if(!isBtClient) {   //如果作为服务端则发送开局信息
                        //send new game info
                        sendMessageByBT(gameInfo.toString());
                    }
                    break;
                case BluetoothService.MESSAGE_TOAST:
                    LogUtil.d(TAG, "MESSAGE_TOAST");
                    switch (msg.getData().getString(BluetoothService.TOAST)){
                        case BluetoothService.UNABLE_CONNECT:
                            mTxtOppositeName.setText(R.string.unable_connect);
                            Toast.makeText(getApplication(), R.string.unable_connect,
                                Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.CONNECT_LOST:
                            mTxtOppositeName.setText(R.string.connect_lost);
                            if(!isDestroyed)
                                isFromMain = true;
                                startSettingActivity();
                            break;
                    }
                    break;
            }
        }
    };

    /**
     * 通信内部类
     */
    public class CommunicationHelper{
        public void send(String message){
            StringBuffer sb = new StringBuffer(TYPE_MOVE);
            sb.append("-");
            sb.append(message);
            sb.append("-");
            LogUtil.d(TAG,"send" + sb.toString());
            sendMessageByBT(sb.toString());
        }

        public void startSettring(){
            isGameing = false;
            isFromMain = true;
            startSettingActivity();
        }

        public void finishActivity(){
            isDestroyed = true;
            GameActivity.this.finish();
        }
    }

}
