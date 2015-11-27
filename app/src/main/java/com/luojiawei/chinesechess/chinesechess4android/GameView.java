package com.luojiawei.chinesechess.chinesechess4android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Stack;

/**
 * Created by L1 on 15-08-21.
 * ��Ϸ���������
 */
public class GameView extends ImageView {
    String Tag = "GameView";
    final int RED = 0, BLACK = 1;                   //ѡ�����ɫ
    public boolean AI = true;                       //�Ƿ��˻���ս
    int screenX, screenY;                           //���̶�Ӧ����Ļ����
    int mScreenW, mScreenH;                         //��Ļ���
    int mChessSize;                                 //���ӳ���
    int mBoardBottom;                               //���̵ױ�
    int posFrom = -1, posTo = -1;                   //���������յ�
    int posFromOpp = -1, posToOpp = -1;             //�Է����������յ�
    String mText = "Text";
    Bitmap mBmChessboard;                           //����
    Bitmap[] mBmAllChess = new Bitmap[14];          //����ͼƬ
    Bitmap mBmSelectBoxRed, mBmSelectBoxBlack;      //ѡ���
    boolean isSelectFrom = false;                   //�Ƿ���ѡ���������
    boolean isFilpped = false;                      //�Ƿ�ת����
    boolean isGameOver = false;                     //�Ƿ���Ϸ����
    boolean isAIThinking = false;                   //�Ƿ��������˼��
    Stack<UndoStack> chessSatck = new Stack<>();    //����ջ
    int[] currentMap = new int[256];                //���浱ǰ���棬������ChessboardUtil.currentMap
    int currentSide;                                //���浱ǰ���巽
    GameActivity.CommunicationHelper mCommunicationHelper;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public void setSendMessage(GameActivity.CommunicationHelper communicationHelper){
        mCommunicationHelper = communicationHelper;
    }

    private void init(Context context) {
        LogUtil.i(Tag,"init()");
        //��ȡ��Ļ�߶ȺͿ��
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenW = dm.widthPixels;
        mScreenH = dm.heightPixels;

        loadResoure();
        ChessboardUtil.startup();   //��ʼ������
        copyCurrentMap();
        mChessSize = mScreenW / 9;    //����9����λ
        mBoardBottom = mChessSize * 10; //����10����λ��������ױ�

        setImageBitmap(Bitmap.createScaledBitmap(mBmSelectBoxRed, mScreenW, mScreenW / 9 * 12, false));   //��ȷ�����ִ�С
        chessSatck.clear();

        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:" + String.valueOf(mChessSize));
    }

    /**
     * ����Ϸ
     */
    public void newGame(boolean isRed, boolean isOffensive) {
        LogUtil.i(Tag, "newGame()  " + isRed + isOffensive);
        if (isAIThinking) {
            return;
        }
        ChessboardUtil.startup();
        posFrom = posTo = posFromOpp = posToOpp = -1;
        isGameOver = false;
        chessSatck.clear();
//        ZobristStruct.initZobrist(ChessboardUtil.zobrPlayer,ChessboardUtil.zobrTable);
        copyCurrentMap();
        if(isRed){  //�ҷ���ɫ
            isFilpped = false;
            if(!isOffensive){   //����
                ChessboardUtil.changeSide();
                if(AI) {
                    //��������
                    AIMove();
                }
            }
        }else { //�ҷ���ɫ
            isFilpped = true;
            if(!isOffensive){   //����
                if(AI) {
                    //��������
                    AIMove();
                }
            }else {
                ChessboardUtil.changeSide();
            }
        }
        invalidate();
    }

    /**
     * ��ת����
     */
    public void flipBoard() {
        LogUtil.i(Tag, "flipBoard()");
        isFilpped = !isFilpped;
        invalidate();
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

    /**
     * invalidate()��������ִ��onDraw()����������ChessboardUtil.currentMap��
     * �ɷ�ֹAI����ʱ��ChessboardUtil.currentMap�ı䣬������������ƴ���
     */
    private void copyCurrentMap() {
        for (int i = 0; i < 256; ++i) {
            currentMap[i] = ChessboardUtil.currentMap[i];
        }
        currentSide = ChessboardUtil.sdPlayer;
    }

    @Override
    public void onDraw(Canvas canvas) {
        //��������
        canvas.drawBitmap(mBmChessboard, null, new Rect(0, 0, mScreenW, mScreenW / 9 * 10), null);

        //��������
        for (int i = 0; i < 256; ++i) {
            if (currentMap[i] != 0) {
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

        //�����ֵ��ķ�����
        final int posBlackSide = 214;
        final int posRedSide = 216;
        screenX = mChessSize * (ChessboardUtil.getCoordX(posRedSide) - ChessboardUtil.BOARD_LEFT);
        screenY = mChessSize * (ChessboardUtil.getCoordY(posRedSide) - ChessboardUtil.BOARD_TOP) + mChessSize / 2;
        canvas.drawBitmap(mBmAllChess[0], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        if(currentSide == ChessboardUtil.RED_MOVE){
            canvas.drawBitmap(mBmSelectBoxBlack, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }
        screenX = mChessSize * (ChessboardUtil.getCoordX(posBlackSide) - ChessboardUtil.BOARD_LEFT);
        screenY = mChessSize * (ChessboardUtil.getCoordY(posBlackSide) - ChessboardUtil.BOARD_TOP) + mChessSize / 2;
        canvas.drawBitmap(mBmAllChess[7], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        if(currentSide == ChessboardUtil.BLACK_MOVE){
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
        if (isFilpped) {  //�����ת������
            int tmpPosition = ChessboardUtil.centreFlip(position);
            screenX = mChessSize * (ChessboardUtil.getCoordX(tmpPosition) - ChessboardUtil.BOARD_LEFT);
            screenY = mChessSize * (ChessboardUtil.getCoordY(tmpPosition) - ChessboardUtil.BOARD_TOP);
        } else {
            screenX = mChessSize * (ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
            screenY = mChessSize * (ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        }
//        LogUtil.i(Tag,"drawPiece Position:"+String.valueOf(position));
//        LogUtil.i(Tag,"drawPiece Flag:"+String.valueOf(currentMap[position]));
        switch (currentMap[position]) {
            case 16:    //��
                canvas.drawBitmap(mBmAllChess[7], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 17:    //ʿ
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
            case 21:    //��
                canvas.drawBitmap(mBmAllChess[12], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 22:    //��
                canvas.drawBitmap(mBmAllChess[13], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 8:    //˧
                canvas.drawBitmap(mBmAllChess[0], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 9:    //��
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
        if (isFilpped) {  //�����ת������
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

    /**
     * ���̵ײ������ı�
     *
     * @param canvas Canvas����
     */
    private void drawText(Canvas canvas) {
        canvas.drawText(mText, 0, mBoardBottom, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getY() >= mBoardBottom) { //  �������̵ײ�
            return false;
        }
        if(isGameOver){
            LogUtil.i(Tag, "Is Game Over!!!");
            gameoverDialog(R.string.is_game_over);
            return false;
        }
        if (isAIThinking) {
            LogUtil.i(Tag, "AI Thinking...");
            showText(R.string.AI_Thinking_disturb);
            return false;
        }

        LogUtil.i(Tag, "Touch:\t(" + String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()) + ")--------------------------------" + this.toString());
        int pos = getPosition(event.getX(), event.getY());  //���������λ��
        LogUtil.i(Tag, "Point:\t" + String.valueOf(pos));
        if (isFilpped) {  //�����ת������
            pos = ChessboardUtil.centreFlip(pos);
        }
        touchEventOnBoard(pos);

        return super.onTouchEvent(event);
    }

    /**
     * ����������ϵ��¼�
     * @param pos λ��
     */
    private void touchEventOnBoard(int pos) {
        int chessFlag = ChessboardUtil.currentMap[pos]; //���λ�õ�����
        LogUtil.i(Tag, "Piece:\t" + String.valueOf(chessFlag));

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
            copyCurrentMap();
            invalidate();   //�ػ�����
        } else if (isSelectFrom && !isGameOver) { // �������Ĳ����Լ����ӣ�������ѡ����(һ�����Լ�����)����ô�������
            int mv;
            if (ChessboardUtil.sdPlayer == 0) {   //�췽����
                posTo = pos;
                mv = ChessboardUtil.getMove(posFrom, posTo); //��ȡ�߷�
            } else {
                posToOpp = pos;
                mv = ChessboardUtil.getMove(posFromOpp, posToOpp); //��ȡ�߷�
            }
            makeAMove(mv);
        }
    }

    /**
     * ������һ����
     * @param mv �ŷ�
     */
    public void oppMove(int mv){
        int vlRep;
        LogUtil.i(Tag, "***OPP*** mv:\t" + String.valueOf(mv));
        LogUtil.i(Tag, "***OPP*** Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
        if (ChessboardUtil.sdPlayer == 0) {   //�췽����
            posFrom = ChessboardUtil.getMoveSrc(mv);
            posTo = ChessboardUtil.getMoveDst(mv);
        } else {
            posFromOpp = ChessboardUtil.getMoveSrc(mv);
            posToOpp = ChessboardUtil.getMoveDst(mv);
        }

        int pcCaptured = ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mv)];
        chessSatck.push(new UndoStack(mv, pcCaptured));   //������ջ
        ChessboardUtil.makeMove(mv);
        copyCurrentMap();
        invalidate();   //�ػ�����
        vlRep = ChessboardUtil.repStatus(3);
        if (Rule.isMate()) {
            // ����ֳ�ʤ������ô����ʤ�������������ҵ���������������ʾ��
            LogUtil.i(Tag, "*********OPP Win*********");
            gameoverDialog(R.string.opposite_win);
            isGameOver = true;
        }else if(vlRep>0){
            vlRep = ChessboardUtil.repValue(vlRep);
            // ע�⣺"vlRep"�Ƕ������˵�ķ�ֵ
            int resId = vlRep < -Engine.WIN_VALUE ? R.string.long_lose
                    : vlRep > Engine.WIN_VALUE ? R.string.long_win : R.string.he_qi;
            gameoverDialog(resId);
            isGameOver = true;
        }else if (ChessboardUtil.nHistoryMoveNum > 100) {
            gameoverDialog(R.string.long_he_qi);
            isGameOver = true;
        }  else {
            // ���û�зֳ�ʤ������ô���Ž��������ӻ�һ�����ӵ�����
            if (ChessboardUtil.captured()) {
                ChessboardUtil.setIrrev();
            }
        }
    }

    /**
     * ��һ����
     * @param mv �ŷ�
     */
    private void makeAMove(int mv) {
        int pcCaptured;
        int vlRep;
        if (!Rule.isLegalMove(mv)) {    //�ŷ������Ϲ���
            showText(R.string.mv_unLegal);
            return;
        }
        pcCaptured = ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mv)]; //��ñ�����
        if (ChessboardUtil.makeMove(mv)) {    //û������������ɹ�
            chessSatck.push(new UndoStack(mv, pcCaptured));   //������ջ
            LogUtil.i(Tag, "mv:\t" + String.valueOf(mv));
            LogUtil.i(Tag, "Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
            isSelectFrom = false;
            copyCurrentMap();   //���浱ǰ���棬��ֹAI����ʱ�����������������
            invalidate();   //�ػ�����

            // ����ظ�����
            vlRep = ChessboardUtil.repStatus(3);
            if (Rule.isMate()) {  //����
                LogUtil.i(Tag, "*********Win*********");
                gameoverDialog(R.string.human_win);
                isGameOver = true;
            }  else if (vlRep > 0) {
                vlRep = ChessboardUtil.repValue(vlRep);
                // ע�⣺"vlRep"�ǶԵ�����˵�ķ�ֵ
                int resId = vlRep < Engine.WIN_VALUE ? R.string.long_lose
                        : vlRep > -Engine.WIN_VALUE ? R.string.long_win : R.string.he_qi;
                gameoverDialog(resId);
                isGameOver = true;
            } else if (ChessboardUtil.nHistoryMoveNum > 100) {
                gameoverDialog(R.string.long_he_qi);
                isGameOver = true;
            } else {
                // ���û�зֳ�ʤ������ô���Ž��������ӻ�һ�����ӵ�����
                if (ChessboardUtil.captured()) {
                    ChessboardUtil.setIrrev();
                }
                if(AI) {    //�˻���ս
                    AIMove();
                }else{  //���˶�ս
                    mCommunicationHelper.send(String.valueOf(mv));
                }
            }
        } else {  //����ʧ�ܣ���������
            LogUtil.i(Tag, "********isCheck*******");
            showText(R.string.is_check);
        }
    }

    public void AIMove() {
        new Thread() {
            @Override
            public void run() {
                isAIThinking = true;
                responseMove(); //���Ի�Ӧһ����
                isAIThinking = false;
            }
        }.start();
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


    /**
     * ���Ի�Ӧһ����
     */
    private void responseMove() {
        int vlRep;
        // ������������һ����
        Engine.searchMain();
        int mv = Engine.mvResult;
        LogUtil.i(Tag, "***AI*** mv:\t" + String.valueOf(mv));
        LogUtil.i(Tag, "***AI*** Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
        if (ChessboardUtil.sdPlayer == 0) {   //�췽����
            posFrom = ChessboardUtil.getMoveSrc(mv);
            posTo = ChessboardUtil.getMoveDst(mv);
        } else {
            posFromOpp = ChessboardUtil.getMoveSrc(mv);
            posToOpp = ChessboardUtil.getMoveDst(mv);
        }

        int pcCaptured = ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mv)];
        chessSatck.push(new UndoStack(mv, pcCaptured));   //������ջ
        ChessboardUtil.makeMove(mv);
        copyCurrentMap();
        postInvalidate();   //���߳����ػ�����
//        invalidate();   //�ػ�����
        vlRep = ChessboardUtil.repStatus(3);
        if (Rule.isMate()) {
            // ����ֳ�ʤ������ô����ʤ�������������ҵ���������������ʾ��
            LogUtil.i(Tag, "*********AI Win*********");
            gameoverDialog(R.string.opposite_win);
            isGameOver = true;
        }else if(vlRep>0){
            vlRep = ChessboardUtil.repValue(vlRep);
            // ע�⣺"vlRep"�Ƕ������˵�ķ�ֵ
            int resId = vlRep < -Engine.WIN_VALUE ? R.string.long_lose
                    : vlRep > Engine.WIN_VALUE ? R.string.long_win : R.string.he_qi;
            gameoverDialog(resId);
            isGameOver = true;
        }else if (ChessboardUtil.nHistoryMoveNum > 100) {
            gameoverDialog(R.string.long_he_qi);
            isGameOver = true;
        }  else {
            // ���û�зֳ�ʤ������ô���Ž��������ӻ�һ�����ӵ�����
            if (ChessboardUtil.captured()) {
                ChessboardUtil.setIrrev();
            }
        }
    }

    /**
     * ����
     */
    public void undo() {
        if(AI){    //�˻�������
            if(isAIThinking || chessSatck.size() < 2){
                return;
            }
        }else if(chessSatck.size() <= 0){
            return;
        }
        UndoStack undoMv = chessSatck.pop();
        ChessboardUtil.undoMovePiece(undoMv.mv, undoMv.pcCaptured);
        ChessboardUtil.changeSide();

        if(AI) {
            undoMv = chessSatck.pop();
            ChessboardUtil.undoMovePiece(undoMv.mv, undoMv.pcCaptured);
            ChessboardUtil.changeSide();
        }

        posFrom = posTo = posFromOpp = posToOpp = -1;
        copyCurrentMap();   //���浱ǰ���棬��ֹAI����ʱ�����������������
        invalidate();   //�ػ�����
    }

    /**
     * �׳���ʾ
     * @param resId string�ļ�id
     */
    public void showText(int resId){
        Toast.makeText(getContext(),resId,Toast.LENGTH_LONG).show();
    }

    /**
     * �׳���ʾ
     * @param msg ��ʾ��Ϣ
     */
    public void showText(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
    }

    /**
     * ��Ϸ����������
     * @param resId ��������Ϣ������Դid
     */
    private void gameoverDialog(int resId){
        // �����˳��Ի���
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        // ���öԻ�����Ϣ
        alertDialog.setMessage(getContext().getString(resId));
        // ���ѡ��ť��ע�����
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.btn_quit), listener);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(R.string.btn_cancel), listener);
        // ��ʾ�Ի���
        alertDialog.show();
    }

    //Dialog����
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:   //ȷ���������
                    mCommunicationHelper.startSettring();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:   //ȡ�����˵���Ϸ������
                    mCommunicationHelper.finishActivity();
                    break;
            }
        }
    };
}
