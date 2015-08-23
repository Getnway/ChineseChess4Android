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
 * ��Ϸ���������
 */
public class GameView extends ImageView {
    String Tag = "GameView";
    int mScreenW, mScreenH; //��Ļ���
    int mChessSize;  //���ӳ���
    int posFrom = -1, posTo = -1;  //���������յ�
    int posFromOpp = -1, posToOpp = -1;  //�Է����������յ�
    Bitmap mBmChessboard;   //����
    Bitmap[] mBmAllChess = new Bitmap[14]; //����
    Bitmap mBmSelectBoxRed, mBmSelectBoxBlack;   //ѡ���
    boolean isSelectFrom = false;   //�Ƿ���ѡ���������

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        //��ȡ��Ļ�߶ȺͿ��
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenW = dm.widthPixels;
        mScreenH = dm.heightPixels;

        loadResoure();
        ChessboardUtil.startup();
        mChessSize = mScreenW / 9;    //����9����λ

//        mBmChessboard = Bitmap.createScaledBitmap(mBmChessboard,mScreenW,mScreenW/9*10,false);

        setImageBitmap(Bitmap.createScaledBitmap(mBmSelectBoxRed, mScreenW, mScreenW / 9 * 10, false));   //��ȷ�����ִ�С

        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:" + String.valueOf(mChessSize));
    }

    public void newGame() {

    }

    /**
     * ������Դ
     */
    private void loadResoure() {
        mBmChessboard = BitmapFactory.decodeResource(getResources(), R.drawable.board2);
        Bitmap selectBox = BitmapFactory.decodeResource(getResources(), R.drawable.select);
        int size = selectBox.getHeight() / 2;
        mBmSelectBoxRed = Bitmap.createBitmap(selectBox, 0, 0, size, size);
        mBmSelectBoxBlack = Bitmap.createBitmap(selectBox, 0, size, size, size);
        Bitmap tmpAllChess = BitmapFactory.decodeResource(getResources(), R.drawable.qz);
        int chessSize = tmpAllChess.getHeight() / 3;  //����ͼƬΪ3��14��
        for (int i = 0; i < 14; ++i) {
            mBmAllChess[i] = Bitmap.createBitmap(tmpAllChess, i * chessSize, 0, chessSize, chessSize);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        //��������
        canvas.drawBitmap(mBmChessboard, null, new Rect(0, 0, mScreenW, mScreenW / 9 * 10), null);

        //��������
        for (int i = 0; i < 256; ++i) {
            if (ChessboardUtil.currentMap[i] != 0) {
                drawPiece(canvas, i);
            }
        }

        int screenX,screenY;
        //����ѡ���
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
     * ��������
     *
     * @param canvas   Canvas����
     * @param position ���������̵�λ��
     */
    private void drawPiece(Canvas canvas, int position) {
        int screenX = mChessSize * (ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
        int screenY = mChessSize * (ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        switch (ChessboardUtil.currentMap[position]) {
            case 16:    //˧
                canvas.drawBitmap(mBmAllChess[7], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 17:    //��
                canvas.drawBitmap(mBmAllChess[8], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 18:    //��
                canvas.drawBitmap(mBmAllChess[9], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 19:    //��
                canvas.drawBitmap(mBmAllChess[10], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 20:    //��
                canvas.drawBitmap(mBmAllChess[11], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 21:    //�h
                canvas.drawBitmap(mBmAllChess[12], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 22:    //��
                canvas.drawBitmap(mBmAllChess[13], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 8:    //��
                canvas.drawBitmap(mBmAllChess[0], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 9:    //ʿ
                canvas.drawBitmap(mBmAllChess[1], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 10:    //��
                canvas.drawBitmap(mBmAllChess[2], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 11:    //��
                canvas.drawBitmap(mBmAllChess[3], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 12:    //��
                canvas.drawBitmap(mBmAllChess[4], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 13:    //��
                canvas.drawBitmap(mBmAllChess[5], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 14:    //��
                canvas.drawBitmap(mBmAllChess[6], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
        }
    }

    /**
     * ����ѡ���
     *
     * @param canvas   Canvas����
     * @param position ѡ��������̵�λ��
     */
    private void drawSelectBox(Canvas canvas, int position) {
        LogUtil.i(Tag,"Form " + String.valueOf(posFrom) + " To " +String.valueOf(posTo));
        LogUtil.i(Tag,"Form " + String.valueOf(posFromOpp) + " To " +String.valueOf(posToOpp));
        int screenX = mChessSize * (ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
        int screenY = mChessSize * (ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        if(ChessboardUtil.sdPlayer == 0){   //�ֵ���ɫ���壬����һ��Ϊ��ɫ���壬���ƺ�ɫѡ���
            canvas.drawBitmap(mBmSelectBoxRed, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }else{
            canvas.drawBitmap(mBmSelectBoxBlack, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.i(Tag, "Touch:(" + String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()) + ")");
        int pos = getPosition(event.getX(), event.getY());  //���������λ��
        LogUtil.i(Tag, "Point:" + String.valueOf(pos));
        int chessFlag = ChessboardUtil.currentMap[pos]; //���λ�õ�����

        // �������Լ����ӣ���ôֱ��ѡ�и���
        if ((chessFlag & ChessboardUtil.getSideTag(ChessboardUtil.sdPlayer)) != 0) {
            if (ChessboardUtil.sdPlayer == 0) {  //�췽����
                posFrom = pos;
                posTo = -1; //ȥ���ϴλ���λ��
            } else {
                posFromOpp = pos;
                posToOpp = -1; //ȥ���ϴλ���λ��
            }
            isSelectFrom = true;    //�����ѡ��
            invalidate();   //�ػ�����
        } else if (isSelectFrom) { // �������Ĳ����Լ����ӣ�������ѡ����(һ�����Լ�����)����ô�������
            int mv;
            if (ChessboardUtil.sdPlayer == 0) {   //�췽����
                posTo = pos;
                mv = ChessboardUtil.getMove(posFrom, posTo); //��ȡ�߷�
            } else {
                posToOpp = pos;
                mv = ChessboardUtil.getMove(posFromOpp, posToOpp); //��ȡ�߷�
            }
            LogUtil.i(Tag, "mv:" + String.valueOf(mv));
            LogUtil.i(Tag, "From " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
            ChessboardUtil.makeMove(mv);    //��һ����
            isSelectFrom = false;
            invalidate();   //�ػ�����
        }

        return super.onTouchEvent(event);
    }

    /**
     * ���ݵ�������ȡ����λ��
     *
     * @param x ���x����
     * @param y ���y����
     * @return ��������λ��
     */
    private int getPosition(float x, float y) {
        return (int) (y / mChessSize + ChessboardUtil.BOARD_LEFT) * 16 + (int) (x / mChessSize + ChessboardUtil.BOARD_TOP);
    }

}
