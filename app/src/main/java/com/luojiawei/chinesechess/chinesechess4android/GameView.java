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
 * ��Ϸ���������
 */
public class GameView extends ImageView {
    String Tag = "GameView";
    int mScreenW, mScreenH; //��Ļ���
    int mBoardMargin;   //�����ϣ��£���Ե��߾�
    int mChessSize;  //���ӳ���
    float clickX, clickY;   //�������
    int row = -1, column = -1;
    Bitmap mBmChessboard;   //����
    Bitmap[] mBmAllChess = new Bitmap[14]; //����
    Bitmap mBmSelectFrom, mBmSelectTo;   //ѡ���
    Rect mBoardDst; //����Ŀ��λ��

    public GameView(Context context) {
        super(context);
        //��ȡ��Ļ�߶ȺͿ��
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenW = dm.widthPixels;
        mScreenH = dm.heightPixels;

        loadResoure();
        ChessboardUtil.startup();
//        float ratioHW = (float) mBmChessboard.getHeight() / mBmChessboard.getWidth();   //����������Դ����߿��
//        mBoardMargin = (int)((mScreenH-mScreenW * ratioHW)/2);  //�ٸ��ݵ�ǰ��Ļ��������Ӧ�߶ȣ�������������ϣ��£���Ե��߾�
        mChessSize = mScreenW / 9;    //����9����λ
        mBoardMargin = (mScreenH - mChessSize * 10) / 2;  //����10����λ
        mBoardDst = new Rect(0, mBoardMargin, mScreenW, mScreenH - mBoardMargin);

        //���ñ���
        setBackgroundResource(R.drawable.bg2);

        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:" + String.valueOf(mChessSize));
    }

    /**
     * ������Դ
     */
    private void loadResoure() {
        mBmChessboard = BitmapFactory.decodeResource(getResources(), R.drawable.board2);
        mBmSelectFrom = BitmapFactory.decodeResource(getResources(), R.drawable.sel);
        mBmSelectTo = BitmapFactory.decodeResource(getResources(), R.drawable.sel);
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

    @Override
    public void onDraw(Canvas canvas) {
        //��������
        canvas.drawBitmap(mBmChessboard, null, mBoardDst, null);

        //��������
        for (int i = 0; i < 256; ++i) {
            if (ChessboardUtil.currentMap[i] != 0) {
                drawPiece(canvas, i);
            }
        }

        //����ѡ���
        if (row >= 0 && column >= 0) {
            canvas.drawBitmap(mBmSelectFrom, null, new Rect(board2ScreenX(column), board2ScreenY(row), board2ScreenX(column) + mChessSize, board2ScreenY(row) + mChessSize), null);
        }
    }

    /**
     * ��������
     * @param canvas Canvas����
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clickX = event.getX();
        clickY = event.getY();
        //�����������
        if (clickY >= mBoardMargin && clickY <= mScreenH - mBoardMargin) {
            column = screen2BoardX(clickX);    //���ݺ�����������ڵڼ���
            row = screen2BoardY(clickY);   //����������������ڵڼ���
            LogUtil.i("GameView", "in :" + String.valueOf(row) + "--" + String.valueOf(column));
        }
        LogUtil.i("GameView", "coord: Y=" + String.valueOf(clickY - mBoardMargin) + "--X=" + String.valueOf(clickX) + "--Size=" + String.valueOf(mChessSize));
        invalidate();   //����onDraw()
        return super.onTouchEvent(event);
    }
}
