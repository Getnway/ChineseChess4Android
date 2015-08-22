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
    int mScreenW, mScreenH; //屏幕宽高
    int mBoardMargin;   //棋盘上（下）边缘外边距
    int mChessSize;  //棋子长宽
    float clickX, clickY;   //点击坐标
    int row = -1, column = -1;
    Bitmap mBmChessboard;
    Bitmap mBmSelectFrom;
    Rect mBoardDst; //棋盘目标位置

    public GameView(Context context) {
        super(context);
        //获取屏幕高度和宽度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenW = dm.widthPixels;
        mScreenH = dm.heightPixels;

        mBmSelectFrom = BitmapFactory.decodeResource(getResources(), R.drawable.sel);
        mBmChessboard = BitmapFactory.decodeResource(getResources(), R.drawable.board);
//        float ratioHW = (float) mBmChessboard.getHeight() / mBmChessboard.getWidth();   //根据棋盘资源算出高宽比
//        mBoardMargin = (int)((mScreenH-mScreenW * ratioHW)/2);  //再根据当前屏幕宽度算出对应高度，进而算出棋盘上（下）边缘外边距
        mChessSize = mScreenW / 9;    //横向9个棋位
        mBoardMargin = (mScreenH - mChessSize * 10) / 2;  //纵向10个棋位
        mBoardDst = new Rect(0, mBoardMargin, mScreenW, mScreenH - mBoardMargin);

        //设置背景
        setBackgroundResource(R.drawable.bg2);

        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:"+String.valueOf(mChessSize));
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBmChessboard, null, mBoardDst, null);    //绘制棋盘
        if (row >= 0 && column >= 0) {
            canvas.drawBitmap(mBmSelectFrom, null, new Rect(column * mChessSize, mBoardMargin + row * mChessSize, column * mChessSize+mChessSize, mBoardMargin + row * mChessSize+mChessSize), null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clickX = event.getX();
        clickY = event.getY();
        //点击在棋盘内
        if (clickY >= mBoardMargin && clickY <= mScreenH - mBoardMargin) {
            column = (int) (clickX / mChessSize);    //根据横坐标计算点击在第几列
            row = (int) ((clickY - mBoardMargin) / mChessSize);   //根据纵坐标计算点击在第几行
            LogUtil.i("GameView", "in :" + String.valueOf(row) + "--" + String.valueOf(column));
        }
        LogUtil.i("GameView", "coord: Y=" + String.valueOf(clickY - mBoardMargin) + "--X=" + String.valueOf(clickX) + "--Size=" + String.valueOf(mChessSize));
        invalidate();   //调用onDraw()
        return super.onTouchEvent(event);
    }
}
