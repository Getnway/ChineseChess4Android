package com.luojiawei.chinesechess.chinesechess4android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

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
    Bitmap mBmChessboard;   //棋盘
    Bitmap[] mBmAllChess = new Bitmap[14]; //棋子
    Bitmap mBmSelectFrom, mBmSelectTo;   //选择框
    Rect mBoardDst; //棋盘目标位置

    public GameView(Context context) {
        super(context);
        //获取屏幕高度和宽度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenW = dm.widthPixels;
        mScreenH = dm.heightPixels;

        loadResoure();
        ChessboardUtil.startup();
//        float ratioHW = (float) mBmChessboard.getHeight() / mBmChessboard.getWidth();   //根据棋盘资源算出高宽比
//        mBoardMargin = (int)((mScreenH-mScreenW * ratioHW)/2);  //再根据当前屏幕宽度算出对应高度，进而算出棋盘上（下）边缘外边距
        mChessSize = mScreenW / 9;    //横向9个棋位
        mBoardMargin = (mScreenH - mChessSize * 10) / 2;  //纵向10个棋位
        mBoardDst = new Rect(0, mBoardMargin, mScreenW, mScreenH - mBoardMargin);

        //设置背景
        setBackgroundResource(R.drawable.bg2);

        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:" + String.valueOf(mChessSize));
    }

    /**
     * 载入资源
     */
    private void loadResoure() {
        mBmChessboard = BitmapFactory.decodeResource(getResources(), R.drawable.board2);
        mBmSelectFrom = BitmapFactory.decodeResource(getResources(), R.drawable.sel);
        mBmSelectTo = BitmapFactory.decodeResource(getResources(), R.drawable.sel);
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

    @Override
    public void onDraw(Canvas canvas) {
        //绘制棋盘
        canvas.drawBitmap(mBmChessboard, null, mBoardDst, null);

        //绘制棋子
        for (int i = 0; i < 256; ++i) {
            if (ChessboardUtil.currentMap[i] != 0) {
                drawPiece(canvas, i);
            }
        }

        //绘制选择框
        if (row >= 0 && column >= 0) {
            canvas.drawBitmap(mBmSelectFrom, null, new Rect(board2ScreenX(column), board2ScreenY(row), board2ScreenX(column) + mChessSize, board2ScreenY(row) + mChessSize), null);
        }
    }

    /**
     * 绘制棋子
     * @param canvas Canvas对象
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clickX = event.getX();
        clickY = event.getY();
        //点击在棋盘内
        if (clickY >= mBoardMargin && clickY <= mScreenH - mBoardMargin) {
            column = screen2BoardX(clickX);    //根据横坐标计算点击在第几列
            row = screen2BoardY(clickY);   //根据纵坐标计算点击在第几行
            LogUtil.i("GameView", "in :" + String.valueOf(row) + "--" + String.valueOf(column));
        }
        LogUtil.i("GameView", "coord: Y=" + String.valueOf(clickY - mBoardMargin) + "--X=" + String.valueOf(clickX) + "--Size=" + String.valueOf(mChessSize));
        invalidate();   //调用onDraw()
        return super.onTouchEvent(event);
    }
}
