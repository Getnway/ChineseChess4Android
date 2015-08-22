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
    int mScreenW, mScreenH; //��Ļ���
    int mBoardMargin;   //�����ϣ��£���Ե��߾�
    int mChessSize;  //���ӳ���
    float clickX, clickY;   //�������
    int row = -1, column = -1;
    Bitmap mBmChessboard;
    Bitmap mBmSelectFrom;
    Rect mBoardDst; //����Ŀ��λ��

    public GameView(Context context) {
        super(context);
        //��ȡ��Ļ�߶ȺͿ��
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenW = dm.widthPixels;
        mScreenH = dm.heightPixels;

        mBmSelectFrom = BitmapFactory.decodeResource(getResources(), R.drawable.sel);
        mBmChessboard = BitmapFactory.decodeResource(getResources(), R.drawable.board);
//        float ratioHW = (float) mBmChessboard.getHeight() / mBmChessboard.getWidth();   //����������Դ����߿��
//        mBoardMargin = (int)((mScreenH-mScreenW * ratioHW)/2);  //�ٸ��ݵ�ǰ��Ļ��������Ӧ�߶ȣ�������������ϣ��£���Ե��߾�
        mChessSize = mScreenW / 9;    //����9����λ
        mBoardMargin = (mScreenH - mChessSize * 10) / 2;  //����10����λ
        mBoardDst = new Rect(0, mBoardMargin, mScreenW, mScreenH - mBoardMargin);

        //���ñ���
        setBackgroundResource(R.drawable.bg2);

        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:"+String.valueOf(mChessSize));
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBmChessboard, null, mBoardDst, null);    //��������
        if (row >= 0 && column >= 0) {
            canvas.drawBitmap(mBmSelectFrom, null, new Rect(column * mChessSize, mBoardMargin + row * mChessSize, column * mChessSize+mChessSize, mBoardMargin + row * mChessSize+mChessSize), null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clickX = event.getX();
        clickY = event.getY();
        //�����������
        if (clickY >= mBoardMargin && clickY <= mScreenH - mBoardMargin) {
            column = (int) (clickX / mChessSize);    //���ݺ�����������ڵڼ���
            row = (int) ((clickY - mBoardMargin) / mChessSize);   //����������������ڵڼ���
            LogUtil.i("GameView", "in :" + String.valueOf(row) + "--" + String.valueOf(column));
        }
        LogUtil.i("GameView", "coord: Y=" + String.valueOf(clickY - mBoardMargin) + "--X=" + String.valueOf(clickX) + "--Size=" + String.valueOf(mChessSize));
        invalidate();   //����onDraw()
        return super.onTouchEvent(event);
    }
}
