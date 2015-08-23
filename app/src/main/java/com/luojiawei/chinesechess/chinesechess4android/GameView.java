package com.luojiawei.chinesechess.chinesechess4android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by L1 on 15-08-21.
 * 游戏界面与控制
 */
public class GameView extends ImageView {
    String Tag = "GameView";
    int mScreenW, mScreenH; //屏幕宽高
    int mBoardMargin;   //棋盘上（下）边缘外边距
    int mChessSize;  //棋子长宽
    float clickX, clickY;   //点击坐标
    int row = -1, column = -1;
    int row2 = -1, column2 = -1;
    Bitmap mBmChessboard;   //棋盘
    Bitmap[] mBmAllChess = new Bitmap[14]; //棋子
    Bitmap mBmSelectBox;   //选择框
    boolean isSelectFrom = false;   //是否已选择棋子起点

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        //获取屏幕高度和宽度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenW = dm.widthPixels;
        mScreenH = dm.heightPixels;

        loadResoure();
        ChessboardUtil.startup();
        mBmChessboard = BitmapFactory.decodeResource(getResources(), R.drawable.board2);
//        float ratioHW = (float) mBmChessboard.getHeight() / mBmChessboard.getWidth();   //根据棋盘资源算出高宽比
//        mBoardMargin = (int)((mScreenH-mScreenW * ratioHW)/2);  //再根据当前屏幕宽度算出对应高度，进而算出棋盘上（下）边缘外边距
        mChessSize = mScreenW / 9;    //横向9个棋位
        mBoardMargin = (mScreenH - mChessSize * 10) / 2;  //纵向10个棋位

//        mBmChessboard = Bitmap.createScaledBitmap(mBmChessboard,mScreenW,mScreenW/9*10,false);

        //设置背景
//        setBackgroundResource(R.drawable.bg2);
        setImageBitmap(Bitmap.createScaledBitmap(mBmChessboard,mScreenW,mScreenW/9*10,false));

        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:" + String.valueOf(mChessSize));
    }

    public void newGame(){

    }

    /**
     * 载入资源
     */
    private void loadResoure() {
        mBmSelectBox = BitmapFactory.decodeResource(getResources(), R.drawable.sel);
        Bitmap tmpAllChess = BitmapFactory.decodeResource(getResources(), R.drawable.qz);
        int chessSize = tmpAllChess.getHeight() / 3;  //棋子图片为3行14列
        for (int i = 0; i < 14; ++i) {
            mBmAllChess[i] = Bitmap.createBitmap(tmpAllChess, i * chessSize, 0, chessSize, chessSize);
        }
    }

    /**
     * 根据棋盘横坐标计算屏幕实际横坐标
     *
     * @param x 棋盘横坐标
     * @return 屏幕实际横坐标
     */
    private int board2ScreenX(int x) {
        return x * mChessSize;
    }

    /**
     * 根据棋盘纵坐标计算屏幕实际纵坐标
     *
     * @param y 棋盘纵坐标
     * @return 屏幕实际纵坐标
     */
    private int board2ScreenY(int y) {
        return y * mChessSize + mBoardMargin;
    }

    /**
     * 根据屏幕点击横坐标计算对应棋盘横坐标
     *
     * @param clickX 屏幕点击横坐标
     * @return 棋盘横坐标
     */
    private int screen2BoardX(float clickX) {
        return (int) (clickX / mChessSize);
    }

    /**
     * 根据屏幕点击纵坐标计算对应棋盘纵坐标
     *
     * @param clickY 屏幕点击纵坐标
     * @return 棋盘纵坐标
     */
    private int screen2BoardY(float clickY) {
        return (int) ((clickY - mBoardMargin) / mChessSize);
    }

    private int getMove(int x, int y) {
        return (x + ChessboardUtil.BOARD_LEFT) * 16 + (y + ChessboardUtil.BOARD_TOP);
    }

    @Override
    public void onDraw(Canvas canvas) {
        mBoardMargin = 0;
        //绘制棋盘
        canvas.drawBitmap(mBmChessboard, null, new Rect(0,0,mScreenW,mScreenW/9*10), null);

        //绘制棋子
        for (int i = 0; i < 256; ++i) {
            if (ChessboardUtil.currentMap[i] != 0) {
                drawPiece(canvas, i);
            }
        }

        //绘制选择框

        if (row >= 0 && column >= 0) {
            LogUtil.i(Tag, "drawFrom");
            isSelectFrom = true;
            canvas.drawBitmap(mBmSelectBox, null, new Rect(board2ScreenX(column), board2ScreenY(row), board2ScreenX(column) + mChessSize, board2ScreenY(row) + mChessSize), null);
        }
        if (isSelectFrom && row2 >= 0 && column2 >= 0) {
            LogUtil.i(Tag, "drawTo");
            isSelectFrom = false;
            canvas.drawBitmap(mBmSelectBox, null, new Rect(board2ScreenX(column2), board2ScreenY(row2), board2ScreenX(column2) + mChessSize, board2ScreenY(row2) + mChessSize), null);
        }
    }


    /**
     * 绘制棋子
     *
     * @param canvas   Canvas对象
     * @param position 棋子在棋盘的位置
     */
    private void drawPiece(Canvas canvas, int position) {
        int screenX = board2ScreenX(ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
        int screenY = board2ScreenY(ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        switch (ChessboardUtil.currentMap[position]) {
            case 16:    //帅
                canvas.drawBitmap(mBmAllChess[0], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 17:    //仕
                canvas.drawBitmap(mBmAllChess[1], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 18:    //相
                canvas.drawBitmap(mBmAllChess[2], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 19:    //马
                canvas.drawBitmap(mBmAllChess[3], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 20:    //车
                canvas.drawBitmap(mBmAllChess[4], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 21:    //砲
                canvas.drawBitmap(mBmAllChess[5], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 22:    //兵
                canvas.drawBitmap(mBmAllChess[6], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 8:    //将
                canvas.drawBitmap(mBmAllChess[7], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 9:    //士
                canvas.drawBitmap(mBmAllChess[8], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 10:    //象
                canvas.drawBitmap(mBmAllChess[9], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 11:    //马
                canvas.drawBitmap(mBmAllChess[10], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 12:    //车
                canvas.drawBitmap(mBmAllChess[11], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 13:    //炮
                canvas.drawBitmap(mBmAllChess[12], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 14:    //卒
                canvas.drawBitmap(mBmAllChess[13], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
        }
    }

    /**
     * 绘制选择框
     * @param canvas Canvas对象
     * @param position 选择框在棋盘的位置
     */
    private void drawSelectBox(Canvas canvas, int position) {
        int screenX = board2ScreenX(ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
        int screenY = board2ScreenY(ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        canvas.drawBitmap(mBmSelectBox, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.i(Tag,"Touch:("+String.valueOf(event.getX())+", "+String.valueOf(event.getY())+")");
        clickX = event.getX();
        clickY = event.getY();
        //点击在棋盘内
        if (clickY >= mBoardMargin && clickY <= mScreenH - mBoardMargin) {
            if (!isSelectFrom) {  //如果还未选择棋子起点
                column = screen2BoardX(clickX);
                row = screen2BoardY(clickY);
                if (ChessboardUtil.currentMap[ChessboardUtil.getCoordPoint(column + 3, row + 3)] != 0) { //选中棋子
                    invalidate();   //调用onDraw()绘制起点选择框
                }
            } else {
                column2 = screen2BoardX(clickX);
                row2 = screen2BoardY(clickY);
                int mv = ((row2 + 3) * 16 + (column2 + 3)) * 256 + ((row + 3) * 16 + (column + 3));
                LogUtil.i(Tag, "mv:" + String.valueOf(mv));
                LogUtil.i(Tag, "From: " + String.valueOf(row) + "-" + String.valueOf(column));
                LogUtil.i(Tag, "To: " + String.valueOf(row2) + "-" + String.valueOf(column2));
                LogUtil.i(Tag, "From " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
                ChessboardUtil.makeMove(mv);
                invalidate();
            }

//            LogUtil.i("GameView", "in :" + String.valueOf(row) + "--" + String.valueOf(column));
        }
//        LogUtil.i("GameView", "coord: Y=" + String.valueOf(clickY - mBoardMargin) + "--X=" + String.valueOf(clickX) + "--Size=" + String.valueOf(mChessSize));
//        invalidate();   //调用onDraw()
        return super.onTouchEvent(event);
    }

    private int getPosition(float x, float y) {

        return 0;
    }
}
