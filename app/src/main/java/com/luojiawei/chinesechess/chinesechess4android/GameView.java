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
import android.util.Size;
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
    int mChessSize;  //棋子长宽
    int posFrom = -1, posTo = -1;  //棋子起点和终点
    int posFromOpp = -1, posToOpp = -1;  //对方棋子起点和终点
    Bitmap mBmChessboard;   //棋盘
    Bitmap[] mBmAllChess = new Bitmap[14]; //棋子
    Bitmap mBmSelectBoxRed, mBmSelectBoxBlack;   //选择框
    boolean isSelectFrom = false;   //是否已选择棋子起点

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attributeSet) {
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
        mChessSize = mScreenW / 9;    //横向9个棋位

//        mBmChessboard = Bitmap.createScaledBitmap(mBmChessboard,mScreenW,mScreenW/9*10,false);

        setImageBitmap(Bitmap.createScaledBitmap(mBmSelectBoxRed, mScreenW, mScreenW / 9 * 10, false));   //可确定布局大小

        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:" + String.valueOf(mChessSize));
    }

    public void newGame() {

    }

    /**
     * 载入资源
     */
    private void loadResoure() {
        mBmChessboard = BitmapFactory.decodeResource(getResources(), R.drawable.board2);
        Bitmap selectBox = BitmapFactory.decodeResource(getResources(), R.drawable.select);
        int size = selectBox.getHeight() / 2;
        mBmSelectBoxRed = Bitmap.createBitmap(selectBox, 0, 0, size, size);
        mBmSelectBoxBlack = Bitmap.createBitmap(selectBox, 0, size, size, size);
        Bitmap tmpAllChess = BitmapFactory.decodeResource(getResources(), R.drawable.qz);
        int chessSize = tmpAllChess.getHeight() / 3;  //棋子图片为3行14列
        for (int i = 0; i < 14; ++i) {
            mBmAllChess[i] = Bitmap.createBitmap(tmpAllChess, i * chessSize, 0, chessSize, chessSize);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        //绘制棋盘
        canvas.drawBitmap(mBmChessboard, null, new Rect(0, 0, mScreenW, mScreenW / 9 * 10), null);

        //绘制棋子
        for (int i = 0; i < 256; ++i) {
            if (ChessboardUtil.currentMap[i] != 0) {
                drawPiece(canvas, i);
            }
        }

        int screenX,screenY;
        //绘制选择框
        if (posFrom >= 0) {
            screenX = mChessSize * (ChessboardUtil.getCoordX(posFrom) - ChessboardUtil.BOARD_LEFT);
            screenY = mChessSize * (ChessboardUtil.getCoordY(posFrom) - ChessboardUtil.BOARD_TOP);
            canvas.drawBitmap(mBmSelectBoxBlack, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }
        if (posTo >= 0) {
            screenX = mChessSize * (ChessboardUtil.getCoordX(posTo) - ChessboardUtil.BOARD_LEFT);
            screenY = mChessSize * (ChessboardUtil.getCoordY(posTo) - ChessboardUtil.BOARD_TOP);
            canvas.drawBitmap(mBmSelectBoxBlack, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }
        if (posFromOpp >= 0) {
            screenX = mChessSize * (ChessboardUtil.getCoordX(posFromOpp) - ChessboardUtil.BOARD_LEFT);
            screenY = mChessSize * (ChessboardUtil.getCoordY(posFromOpp) - ChessboardUtil.BOARD_TOP);
            canvas.drawBitmap(mBmSelectBoxRed, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }
        if (posToOpp >= 0) {
            screenX = mChessSize * (ChessboardUtil.getCoordX(posToOpp) - ChessboardUtil.BOARD_LEFT);
            screenY = mChessSize * (ChessboardUtil.getCoordY(posToOpp) - ChessboardUtil.BOARD_TOP);
            canvas.drawBitmap(mBmSelectBoxRed, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }

    }

    /**
     * 绘制棋子
     *
     * @param canvas   Canvas对象
     * @param position 棋子在棋盘的位置
     */
    private void drawPiece(Canvas canvas, int position) {
        int screenX = mChessSize * (ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
        int screenY = mChessSize * (ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        switch (ChessboardUtil.currentMap[position]) {
            case 16:    //帅
                canvas.drawBitmap(mBmAllChess[7], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 17:    //仕
                canvas.drawBitmap(mBmAllChess[8], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 18:    //相
                canvas.drawBitmap(mBmAllChess[9], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 19:    //马
                canvas.drawBitmap(mBmAllChess[10], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 20:    //车
                canvas.drawBitmap(mBmAllChess[11], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 21:    //砲
                canvas.drawBitmap(mBmAllChess[12], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 22:    //兵
                canvas.drawBitmap(mBmAllChess[13], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 8:    //将
                canvas.drawBitmap(mBmAllChess[0], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 9:    //士
                canvas.drawBitmap(mBmAllChess[1], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 10:    //象
                canvas.drawBitmap(mBmAllChess[2], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 11:    //马
                canvas.drawBitmap(mBmAllChess[3], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 12:    //车
                canvas.drawBitmap(mBmAllChess[4], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 13:    //炮
                canvas.drawBitmap(mBmAllChess[5], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 14:    //卒
                canvas.drawBitmap(mBmAllChess[6], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
        }
    }

    /**
     * 绘制选择框
     *
     * @param canvas   Canvas对象
     * @param position 选择框在棋盘的位置
     */
    private void drawSelectBox(Canvas canvas, int position) {
        LogUtil.i(Tag,"Form " + String.valueOf(posFrom) + " To " +String.valueOf(posTo));
        LogUtil.i(Tag,"Form " + String.valueOf(posFromOpp) + " To " +String.valueOf(posToOpp));
        int screenX = mChessSize * (ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
        int screenY = mChessSize * (ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        if(ChessboardUtil.sdPlayer == 0){   //轮到红色走棋，即上一步为黑色走棋，绘制红色选择框
            canvas.drawBitmap(mBmSelectBoxRed, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }else{
            canvas.drawBitmap(mBmSelectBoxBlack, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.i(Tag, "Touch:(" + String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()) + ")");
        int pos = getPosition(event.getX(), event.getY());  //点击的棋盘位置
        LogUtil.i(Tag, "Point:" + String.valueOf(pos));
        int chessFlag = ChessboardUtil.currentMap[pos]; //点击位置的棋子

        // 如果点击自己的子，那么直接选中该子
        if ((chessFlag & ChessboardUtil.getSideTag(ChessboardUtil.sdPlayer)) != 0) {
            if (ChessboardUtil.sdPlayer == 0) {  //红方走棋
                posFrom = pos;
                posTo = -1; //去除上次绘制位置
            } else {
                posFromOpp = pos;
                posToOpp = -1; //去除上次绘制位置
            }
            isSelectFrom = true;    //标记已选子
            invalidate();   //重绘棋盘
        } else if (isSelectFrom) { // 如果点击的不是自己的子，但有子选中了(一定是自己的子)，那么走这个子
            int mv;
            if (ChessboardUtil.sdPlayer == 0) {   //红方走棋
                posTo = pos;
                mv = ChessboardUtil.getMove(posFrom, posTo); //获取走法
            } else {
                posToOpp = pos;
                mv = ChessboardUtil.getMove(posFromOpp, posToOpp); //获取走法
            }
            LogUtil.i(Tag, "mv:" + String.valueOf(mv));
            LogUtil.i(Tag, "From " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
            ChessboardUtil.makeMove(mv);    //走一步棋
            isSelectFrom = false;
            invalidate();   //重绘棋盘
        }

        return super.onTouchEvent(event);
    }

    /**
     * 根据点击坐标获取棋盘位置
     *
     * @param x 点击x坐标
     * @param y 点击y坐标
     * @return 返回棋盘位置
     */
    private int getPosition(float x, float y) {
        return (int) (y / mChessSize + ChessboardUtil.BOARD_LEFT) * 16 + (int) (x / mChessSize + ChessboardUtil.BOARD_TOP);
    }

}
