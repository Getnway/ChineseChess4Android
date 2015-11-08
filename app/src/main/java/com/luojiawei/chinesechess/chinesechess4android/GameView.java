package com.luojiawei.chinesechess.chinesechess4android;

import android.content.Context;
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
 * 游戏界面与控制
 */
public class GameView extends ImageView {
    String Tag = "GameView";
    final int RED = 0, BLACK = 1;  //选择框颜色
    public boolean AI = true;   //是否人机对战
    int screenX, screenY;   //棋盘对应的屏幕坐标
    int mScreenW, mScreenH; //屏幕宽高
    int mChessSize;  //棋子长宽
    int mBoardBottom;   //棋盘底边
    int posFrom = -1, posTo = -1;  //棋子起点和终点
    int posFromOpp = -1, posToOpp = -1;  //对方棋子起点和终点
    String mText = "Text";
    Bitmap mBmChessboard;   //棋盘
    Bitmap[] mBmAllChess = new Bitmap[14]; //棋子
    Bitmap mBmSelectBoxRed, mBmSelectBoxBlack;   //选择框
    boolean isSelectFrom = false;   //是否已选择棋子起点
    boolean isFilpped = false;  //是否翻转棋盘
    boolean isGameOver = false; //是否游戏结束
    boolean isAIThinking = false;
    Stack<UndoStack> chessSatck = new Stack<>();
    Thread searchThread;
    int[] currentMap = new int[256];    //保存当前局面，拷贝自ChessboardUtil.currentMap

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        LogUtil.i(Tag,"init()");
        //获取屏幕高度和宽度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenW = dm.widthPixels;
        mScreenH = dm.heightPixels;

        loadResoure();
        ChessboardUtil.startup();   //初始化棋盘
        copyCurrentMap();
        mChessSize = mScreenW / 9;    //横向9个棋位
        mBoardBottom = mChessSize * 10; //纵向10个棋位，计算出底边

        setImageBitmap(Bitmap.createScaledBitmap(mBmSelectBoxRed, mScreenW, mScreenW / 9 * 12, false));   //可确定布局大小
        chessSatck.clear();

//        searchThread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    isAIThinking = true;
//                    responseMove();
//                    isAIThinking = false;
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        };
        LogUtil.i("GameView", "Screen:" + String.valueOf(mScreenH) + " X " + String.valueOf(mScreenW));
        LogUtil.i("GameView", "ChessSize:" + String.valueOf(mChessSize));
    }

    /**
     * 新游戏
     */
    public void newGame() {
        LogUtil.i(Tag, "newGame()  " + this.toString());
        if (isAIThinking) {
            return;
        }
        ChessboardUtil.startup();
        posFrom = posTo = posFromOpp = posToOpp = -1;
        isGameOver = false;
        chessSatck.clear();
//        ZobristStruct.initZobrist(ChessboardUtil.zobrPlayer,ChessboardUtil.zobrTable);
        copyCurrentMap();
        invalidate();
    }

    /**
     * 翻转棋盘
     */
    public void flipBoard() {
        LogUtil.i(Tag, "flipBoard()");
        isFilpped = !isFilpped;
        invalidate();
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

    /**
     * invalidate()并非立即执行onDraw()，复制数组ChessboardUtil.currentMap，
     * 可防止AI搜索时将ChessboardUtil.currentMap改变，而发生界面绘制错误
     */
    private void copyCurrentMap() {
        for (int i = 0; i < 256; ++i) {
            currentMap[i] = ChessboardUtil.currentMap[i];
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        //绘制棋盘
        canvas.drawBitmap(mBmChessboard, null, new Rect(0, 0, mScreenW, mScreenW / 9 * 10), null);

        //绘制棋子
        for (int i = 0; i < 256; ++i) {
            if (currentMap[i] != 0) {
                drawPiece(canvas, i);
            }
        }

        //绘制选择框
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

        //绘制轮到哪方走棋
        final int posBlackSide = 214;
        final int posRedSide = 216;
        screenX = mChessSize * (ChessboardUtil.getCoordX(posRedSide) - ChessboardUtil.BOARD_LEFT);
        screenY = mChessSize * (ChessboardUtil.getCoordY(posRedSide) - ChessboardUtil.BOARD_TOP) + mChessSize / 2;
        canvas.drawBitmap(mBmAllChess[0], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        if(ChessboardUtil.sdPlayer == ChessboardUtil.RED_MOVE){
            canvas.drawBitmap(mBmSelectBoxBlack, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }
        screenX = mChessSize * (ChessboardUtil.getCoordX(posBlackSide) - ChessboardUtil.BOARD_LEFT);
        screenY = mChessSize * (ChessboardUtil.getCoordY(posBlackSide) - ChessboardUtil.BOARD_TOP) + mChessSize / 2;
        canvas.drawBitmap(mBmAllChess[7], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        if(ChessboardUtil.sdPlayer == ChessboardUtil.BLACK_MOVE){
            canvas.drawBitmap(mBmSelectBoxRed, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }


        //绘制文本
//        canvas.drawLine(0, mBoardBottom, 50, mBoardBottom + 50,null);
//        mText = "haha";
//        drawText(canvas);
    }

    /**
     * 绘制棋子
     *
     * @param canvas   Canvas对象
     * @param position 棋子在棋盘的位置
     */
    private void drawPiece(Canvas canvas, int position) {
        if (isFilpped) {  //如果翻转了棋盘
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
            case 16:    //将
                canvas.drawBitmap(mBmAllChess[7], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 17:    //士
                canvas.drawBitmap(mBmAllChess[8], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 18:    //象
                canvas.drawBitmap(mBmAllChess[9], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 19:    //马
                canvas.drawBitmap(mBmAllChess[10], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 20:    //车
                canvas.drawBitmap(mBmAllChess[11], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 21:    //炮
                canvas.drawBitmap(mBmAllChess[12], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 22:    //卒
                canvas.drawBitmap(mBmAllChess[13], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 8:    //帅
                canvas.drawBitmap(mBmAllChess[0], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 9:    //仕
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
            case 13:    //跑
                canvas.drawBitmap(mBmAllChess[5], null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
                break;
            case 14:    //兵
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
    private void drawSelectBox(Canvas canvas, int position, int color) {
        if (isFilpped) {  //如果翻转了棋盘
            position = ChessboardUtil.centreFlip(position);
        }
        LogUtil.i(Tag, "SelectBox:\t" + String.valueOf(ChessboardUtil.getMoveSrc(position)));
        screenX = mChessSize * (ChessboardUtil.getCoordX(position) - ChessboardUtil.BOARD_LEFT);
        screenY = mChessSize * (ChessboardUtil.getCoordY(position) - ChessboardUtil.BOARD_TOP);
        if (color == RED) {   //轮到红色走棋，即上一步为黑色走棋，绘制红色选择框
            canvas.drawBitmap(mBmSelectBoxRed, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        } else {
            canvas.drawBitmap(mBmSelectBoxBlack, null, new Rect(screenX, screenY, screenX + mChessSize, screenY + mChessSize), null);
        }
    }

    /**
     * 棋盘底部绘制文本
     *
     * @param canvas Canvas对象
     */
    private void drawText(Canvas canvas) {
        canvas.drawText(mText, 0, mBoardBottom, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getY() >= mBoardBottom) { //  超过棋盘底部
            return false;
        }
        if(isGameOver){
            LogUtil.i(Tag, "Is Game Over!!!");
            showText(R.string.is_game_over);
            return false;
        }
//        if(AI){
            if (isAIThinking) {
                LogUtil.i(Tag, "AI Thinking...");
                showText(R.string.AI_Thinking_disturb);
                return false;
            }
//        }else{

//        }
        LogUtil.i(Tag, "Touch:\t(" + String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()) + ")--------------------------------" + this.toString());
        int pos = getPosition(event.getX(), event.getY());  //点击的棋盘位置
        LogUtil.i(Tag, "Point:\t" + String.valueOf(pos));
        if (isFilpped) {  //如果翻转了棋盘
            pos = ChessboardUtil.centreFlip(pos);
        }
        touchEventOnBoard(pos);

        return super.onTouchEvent(event);
    }

    //点击在棋盘上的事件
    private void touchEventOnBoard(int pos) {
        int chessFlag = ChessboardUtil.currentMap[pos]; //点击位置的棋子
        LogUtil.i(Tag, "Piece:\t" + String.valueOf(chessFlag));

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
            copyCurrentMap();
            invalidate();   //重绘棋盘
        } else if (isSelectFrom && !isGameOver) { // 如果点击的不是自己的子，但有子选中了(一定是自己的子)，那么走这个子
            int mv;
            if (ChessboardUtil.sdPlayer == 0) {   //红方走棋
                posTo = pos;
                mv = ChessboardUtil.getMove(posFrom, posTo); //获取走法
            } else {
                posToOpp = pos;
                mv = ChessboardUtil.getMove(posFromOpp, posToOpp); //获取走法
            }
            makeAMove(mv);
        }
    }

    /**
     * 走一步棋
     * @param mv 着法
     */
    private void makeAMove(int mv) {
        int pcCaptured;
        int vlRep;
        if (!Rule.isLegalMove(mv)) {    //着法不符合规则
            showText(R.string.mv_unLegal);
            return;
        }
        pcCaptured = ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mv)]; //获得被吃子
        if (ChessboardUtil.makeMove(mv)) {    //没被将军，走棋成功
            chessSatck.push(new UndoStack(mv, pcCaptured));   //下棋入栈
            LogUtil.i(Tag, "mv:\t" + String.valueOf(mv));
            LogUtil.i(Tag, "Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
            isSelectFrom = false;
            copyCurrentMap();   //保存当前界面，防止AI搜索时，发生错误绘制棋盘
            invalidate();   //重绘棋盘

            // 检查重复局面
            vlRep = ChessboardUtil.repStatus(3);
            if (Rule.isMate()) {  //将死
//                        Toast.makeText(getContext(), R.string.is_win,Toast.LENGTH_LONG);/
                LogUtil.i(Tag, "*********Win*********");
                showText(R.string.human_win);
                isGameOver = true;
            }  else if (vlRep > 0) {
                vlRep = ChessboardUtil.repValue(vlRep);
                // 注意："vlRep"是对电脑来说的分值
                String str = vlRep > Engine.WIN_VALUE ? "长将作负，请不要气馁！" :
                        vlRep < -Engine.WIN_VALUE ? "对方长将作负，祝贺你取得胜利！" : "双方不变作和，辛苦了！";
                showText(str);
                isGameOver = true;
            } else if (ChessboardUtil.nHistoryMoveNum > 100) {
                String str = "超过自然限着作和，辛苦了！";
                showText(str);
                isGameOver = true;
            } else {
                // 如果没有分出胜负，那么播放将军、吃子或一般走子的声音
                if (ChessboardUtil.captured()) {
                    ChessboardUtil.setIrrev();
                }
                if(AI) {    //人机对战
                    new Thread() {
                        @Override
                        public void run() {
                            isAIThinking = true;
                            responseMove(); //电脑回应一步棋
                            isAIThinking = false;
                        }
                    }.start();
                }else{  //人人对战

                }
            }
        } else {  //走棋失败，被将军中
            LogUtil.i(Tag, "********isCheck*******");
            showText(R.string.is_check);
        }
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


    /**
     * 电脑回应一步棋
     */
    void responseMove() {
        int vlRep;
        // 电脑搜索并走一步棋
        Engine.searchMain();
        int mv = Engine.mvResult;
        LogUtil.i(Tag, "***AI*** mv:\t" + String.valueOf(mv));
        LogUtil.i(Tag, "***AI*** Piece:\tFrom " + String.valueOf(ChessboardUtil.getMoveSrc(mv)) + " To " + String.valueOf(ChessboardUtil.getMoveDst(mv)));
        if (ChessboardUtil.sdPlayer == 0) {   //红方走棋
            posFrom = ChessboardUtil.getMoveSrc(mv);
            posTo = ChessboardUtil.getMoveDst(mv);
        } else {
            posFromOpp = ChessboardUtil.getMoveSrc(mv);
            posToOpp = ChessboardUtil.getMoveDst(mv);
        }

        int pcCaptured = ChessboardUtil.currentMap[ChessboardUtil.getMoveDst(mv)];
        chessSatck.push(new UndoStack(mv, pcCaptured));   //下棋入栈
        ChessboardUtil.makeMove(mv);
        copyCurrentMap();
        postInvalidate();   //主线程外重绘棋盘
//        invalidate();   //重绘棋盘
        vlRep = ChessboardUtil.repStatus(3);
        if (Rule.isMate()) {
            // 如果分出胜负，那么播放胜负的声音，并且弹出不带声音的提示框
            LogUtil.i(Tag, "*********AI Win*********");
            showText(R.string.opposite_win);
            isGameOver = true;
        }else if(vlRep>0){
            vlRep = ChessboardUtil.repValue(vlRep);
            // 注意："vlRep"是对玩家来说的分值
            String str = vlRep < -Engine.WIN_VALUE ? "长将作负，请不要气馁！"
                    : vlRep > Engine.WIN_VALUE ? "对方长将作负，祝贺你取得胜利！" : "双方不变作和，辛苦了！";
            showText(str);
            isGameOver = true;

        }else if (ChessboardUtil.nHistoryMoveNum > 100) {
            String str = "超过自然限着作和，辛苦了！";
            showText(str);
            isGameOver = true;
        }  else {
            // 如果没有分出胜负，那么播放将军、吃子或一般走子的声音
            if (ChessboardUtil.captured()) {
                ChessboardUtil.setIrrev();
            }
        }
    }

    /**
     * 悔棋
     */
    public void undo() {
        if(AI){    //人机悔两步
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
        copyCurrentMap();   //保存当前界面，防止AI搜索时，发生错误绘制棋盘
        invalidate();   //重绘棋盘
    }

    /**
     * 抛出提示
     * @param resId string文件id
     */
    public void showText(int resId){
        Toast.makeText(getContext(),resId,Toast.LENGTH_LONG).show();
    }

    /**
     * 抛出提示
     * @param msg 提示信息
     */
    public void showText(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
    }

}
