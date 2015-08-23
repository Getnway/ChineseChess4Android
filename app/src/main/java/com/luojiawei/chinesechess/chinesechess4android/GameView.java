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
    int RED = 0, BLACK = 1;  //ѡ�����ɫ
    int screenX, screenY;   //���̶�Ӧ����Ļ����
    int mScreenW, mScreenH; //��Ļ���
    int mChessSize;  //���ӳ���
    int posFrom = -1, posTo = -1;  //���������յ�
    int posFromOpp = -1, posToOpp = -1;  //�Է����������յ�
    Bitmap mBmChessboard;   //����
    Bitmap[] mBmAllChess = new Bitmap[14]; //����
    Bitmap mBmSelectBoxRed, mBmSelectBoxBlack;   //ѡ���
    boolean isSelectFrom = false;   //�Ƿ���ѡ���������
    boolean isFilpped = false;  //�Ƿ�ת����

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

        //����ѡ���
        if (posFrom >= 0) {
            drawSelectBox(canvas, posFrom, BLACK);
        }
        if (posTo >= 0) {
            drawSelectBox(canvas, posTo, BLACK);
        }
        if (posFromOpp >= 0) {
            drawSelectBox(canvas, posFromOpp, RED);
        }
        if (posToOpp >= 0) {
            drawSelectBox(canvas, posToOpp, RED);
        }

    }

    /**
     * ��������
     *
     * @param canvas   Canvas����
     * @param position ���������̵�λ��
     */
    private void drawPiece(Canvas canvas, int position) {
        if(isFilpped){  //�����ת������
            int tmpPosition = ChessboardUtil.centreFlip(position);
            screenX = mChessSize * (ChessboardUtil.getCoordX(tmpPosition) - ChessboardUtil.BOARD_LEFT);
            screenY = mChessSize * (ChessboardUtil.getCoordY(tmpPosition) - ChessboardUtil.BOARD_TOP);
        }else{
            screenX = mChessSize * (ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
            screenY = mChessSize * (ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        }
//        LogUtil.i(Tag,"drawPiece Position:"+String.valueOf(position));
//        LogUtil.i(Tag,"drawPiece Flag:"+String.valueOf(ChessboardUtil.currentMap[position]));
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
    private void drawSelectBox(Canvas canvas, int position, int color) {
        if(isFilpped){  //�����ת������
            position = ChessboardUtil.centreFlip(position);
        }
        LogUtil.i(Tag, "SelectBox:\t" + String.valueOf(ChessboardUtil.getMoveSrc(position)));
        screenX = mChessSize * (ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
        screenY = mChessSize * (ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        if (color == RED) {   //�ֵ���ɫ���壬����һ��Ϊ��ɫ���壬���ƺ�ɫѡ���
            canvas.drawBitmap(mBmSelectBoxRed, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        } else {
            canvas.drawBitmap(mBmSelectBoxBlack, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.i(Tag, "Touch:\t(" + String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()) + ")--------------------------------");
        int pos = getPosition(event.getX(), event.getY());  //���������λ��
        LogUtil.i(Tag, "Point:\t" + String.valueOf(pos));
        if(isFilpped){  //�����ת������
            pos = ChessboardUtil.centreFlip(pos);
        }
        LogUtil.i(Tag,"Piece:\t"+String.valueOf(ChessboardUtil.currentMap[pos]));
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
            LogUtil.i(Tag, "mv:\t" + String.valueOf(mv));
            LogUtil.i(Tag, "Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
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
