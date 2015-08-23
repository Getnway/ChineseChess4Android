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
 * ��Ϸ���������
 */
public class GameView extends ImageView {
    String Tag = "GameView";
    int mScreenW, mScreenH; //��Ļ���
    int mBoardMargin;   //�����ϣ��£���Ե��߾�
    int mChessSize;  //���ӳ���
    float clickX, clickY;   //�������
    int row = -1, column = -1;
    int row2 = -1, column2 = -1;
    Bitmap mBmChessboard;   //����
    Bitmap[] mBmAllChess = new Bitmap[14]; //����
    Bitmap mBmSelectBox;   //ѡ���
    boolean isSelectFrom = false;   //�Ƿ���ѡ���������

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attributeSet){
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
        mBmChessboard = BitmapFactory.decodeResource(getResources(), R.drawable.board2);
//        float ratioHW = (float) mBmChessboard.getHeight() / mBmChessboard.getWidth();   //����������Դ����߿��
//        mBoardMargin = (int)((mScreenH-mScreenW * ratioHW)/2);  //�ٸ��ݵ�ǰ��Ļ��������Ӧ�߶ȣ�������������ϣ��£���Ե��߾�
        mChessSize = mScreenW / 9;    //����9����λ
        mBoardMargin = (mScreenH - mChessSize * 10) / 2;  //����10����λ

//        mBmChessboard = Bitmap.createScaledBitmap(mBmChessboard,mScreenW,mScreenW/9*10,false);

        //���ñ���
//        setBackgroundResource(R.drawable.bg2);
        setImageBitmap(Bitmap.createScaledBitmap(mBmChessboard,mScreenW,mScreenW/9*10,false));

        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:" + String.valueOf(mChessSize));
    }

    public void newGame(){

    }

    /**
     * ������Դ
     */
    private void loadResoure() {
        mBmSelectBox = BitmapFactory.decodeResource(getResources(), R.drawable.sel);
        Bitmap tmpAllChess = BitmapFactory.decodeResource(getResources(), R.drawable.qz);
        int chessSize = tmpAllChess.getHeight() / 3;  //����ͼƬΪ3��14��
        for (int i = 0; i < 14; ++i) {
            mBmAllChess[i] = Bitmap.createBitmap(tmpAllChess, i * chessSize, 0, chessSize, chessSize);
        }
    }

    /**
     * �������̺����������Ļʵ�ʺ�����
     *
     * @param x ���̺�����
     * @return ��Ļʵ�ʺ�����
     */
    private int board2ScreenX(int x) {
        return x * mChessSize;
    }

    /**
     * �������������������Ļʵ��������
     *
     * @param y ����������
     * @return ��Ļʵ��������
     */
    private int board2ScreenY(int y) {
        return y * mChessSize + mBoardMargin;
    }

    /**
     * ������Ļ�������������Ӧ���̺�����
     *
     * @param clickX ��Ļ���������
     * @return ���̺�����
     */
    private int screen2BoardX(float clickX) {
        return (int) (clickX / mChessSize);
    }

    /**
     * ������Ļ�������������Ӧ����������
     *
     * @param clickY ��Ļ���������
     * @return ����������
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
        //��������
        canvas.drawBitmap(mBmChessboard, null, new Rect(0,0,mScreenW,mScreenW/9*10), null);

        //��������
        for (int i = 0; i < 256; ++i) {
            if (ChessboardUtil.currentMap[i] != 0) {
                drawPiece(canvas, i);
            }
        }

        //����ѡ���

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
     * ��������
     *
     * @param canvas   Canvas����
     * @param position ���������̵�λ��
     */
    private void drawPiece(Canvas canvas, int position) {
        int screenX = board2ScreenX(ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
        int screenY = board2ScreenY(ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        switch (ChessboardUtil.currentMap[position]) {
            case 16:    //˧
                canvas.drawBitmap(mBmAllChess[0], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 17:    //��
                canvas.drawBitmap(mBmAllChess[1], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 18:    //��
                canvas.drawBitmap(mBmAllChess[2], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 19:    //��
                canvas.drawBitmap(mBmAllChess[3], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 20:    //��
                canvas.drawBitmap(mBmAllChess[4], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 21:    //�h
                canvas.drawBitmap(mBmAllChess[5], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 22:    //��
                canvas.drawBitmap(mBmAllChess[6], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 8:    //��
                canvas.drawBitmap(mBmAllChess[7], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 9:    //ʿ
                canvas.drawBitmap(mBmAllChess[8], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 10:    //��
                canvas.drawBitmap(mBmAllChess[9], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 11:    //��
                canvas.drawBitmap(mBmAllChess[10], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 12:    //��
                canvas.drawBitmap(mBmAllChess[11], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 13:    //��
                canvas.drawBitmap(mBmAllChess[12], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 14:    //��
                canvas.drawBitmap(mBmAllChess[13], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
        }
    }

    /**
     * ����ѡ���
     * @param canvas Canvas����
     * @param position ѡ��������̵�λ��
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
        //�����������
        if (clickY >= mBoardMargin && clickY <= mScreenH - mBoardMargin) {
            if (!isSelectFrom) {  //�����δѡ���������
                column = screen2BoardX(clickX);
                row = screen2BoardY(clickY);
                if (ChessboardUtil.currentMap[ChessboardUtil.getCoordPoint(column + 3, row + 3)] != 0) { //ѡ������
                    invalidate();   //����onDraw()�������ѡ���
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
//        invalidate();   //����onDraw()
        return super.onTouchEvent(event);
    }

    private int getPosition(float x, float y) {

        return 0;
    }
}
