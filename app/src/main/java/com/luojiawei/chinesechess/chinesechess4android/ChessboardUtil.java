package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-08-23.
 */
public class ChessboardUtil {
    static String TAG = "ChessboardUtil";
    public static int sdPlayer;                   // 轮到谁走，0=红方，1=黑方
    public static final int RED_MOVE = 0;         //轮到红方走棋
    public static final int BLACK_MOVE = 1;       //轮到黑方走棋
    public static int[] currentMap = new int[256];          // 棋盘上的棋子

    final static int MAX_MOVES = 256;     // 最大的历史走法数
    final static int DRAW_VALUE = 20;     // 和棋时返回的分数(取负值)
    final static int NULL_MARGIN = 400;   // 空步裁剪的子力边界

    static int nHistoryMoveNum;    //历史走法数
    // 棋盘范围
    public final static int BOARD_TOP = 3;
    public final static int BOARD_BOTTOM = 12;
    public final static int BOARD_LEFT = 3;
    public final static int BOARD_RIGHT = 11;

    static ZobristStruct zobrPlayer = new ZobristStruct();
    static ZobristStruct[][] zobrTable = new ZobristStruct[14][256];
    static ZobristStruct zobr = new ZobristStruct();
    static MoveStruct[] mvsList = new MoveStruct[MAX_MOVES];

    static {
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 256; j++) {
                zobrTable[i][j] = new ZobristStruct();
            }
        }
        for(int i=0;i<MAX_MOVES;i++){
            mvsList[i] = new MoveStruct();
        }
        ZobristStruct.initZobrist(zobrPlayer,zobrTable);
    }


    public static void startup() {            // 初始化棋盘
        LogUtil.i(TAG, "startup()");
        LogUtil.i("Engine", "----------------------newGame()---------------------- ");
        int sq, pc;
        sdPlayer = 0;
        Engine.nDistance = 0;
        Value.vlBlack = Value.vlWhite = 0;
        zobr.initZero();
        LogUtil.i(TAG, "initZobr()");
        for (sq = 0; sq < 256; sq++) {
            pc = startupMap[sq];
            if (pc != 0) {
                addPiece(sq, pc);
            } else {
                currentMap[sq] = 0;
            }
        }
        setIrrev();
    }

    public static void setIrrev() {           // 清空(初始化)历史走法信息
        LogUtil.i(TAG, "setIrrev()");
        mvsList[0].set(0, 0, Rule.isChecked(), zobr.key);
        nHistoryMoveNum = 1;
    }

    public static void changeSide() {         // 交换走子方
        sdPlayer = 1 - sdPlayer;
        zobr.xor(zobrPlayer);
    }

    public static void addPiece(int sq, int pc) { // 在棋盘上放一枚棋子
        currentMap[sq] = pc;
        // 红方加分，黑方(注意"cucvlPiecePos"取值要颠倒)减分，因为子力表只有一个方向的位置子力
        if (pc < 16) {
            Value.vlWhite += Value.cucvlPiecePos[pc - 8][sq];
            zobr.xor(zobrTable[pc - 8][sq]);
        } else {
            Value.vlBlack += Value.cucvlPiecePos[pc - 16][centreFlip(sq)];
            zobr.xor(zobrTable[pc - 9][sq]);
        }
    }

    public static void delPiece(int sq, int pc) {         // 从棋盘上拿走一枚棋子
        currentMap[sq] = 0;
        // 红方减分，黑方(注意"cucvlPiecePos"取值要颠倒)加分，因为子力表只有一个方向的位置子力
        if (pc < 16) {
            Value.vlWhite -= Value.cucvlPiecePos[pc - 8][sq];
            zobr.xor(zobrTable[pc - 8][sq]);
        } else {
            Value.vlBlack -= Value.cucvlPiecePos[pc - 16][centreFlip(sq)];
            zobr.xor(zobrTable[pc - 9][sq]);
        }
    }

    public static int movePiece(int mv) {         // 搬一步棋的棋子
        int sqSrc, sqDst, pc, pcCaptured;
        sqSrc = getMoveSrc(mv);
        sqDst = getMoveDst(mv);
        pcCaptured = currentMap[sqDst];
        if (pcCaptured != 0) {
            delPiece(sqDst, pcCaptured);
        }
        pc = currentMap[sqSrc];
        delPiece(sqSrc, pc);
        addPiece(sqDst, pc);
        return pcCaptured;
    }

    public static void undoMovePiece(int mv, int pcCaptured) {      // 撤消搬一步棋的棋子
        int sqSrc, sqDst, pc;
        sqSrc = getMoveSrc(mv);
        sqDst = getMoveDst(mv);
        pc = currentMap[sqDst];
        delPiece(sqDst, pc);
        addPiece(sqSrc, pc);
        if (pcCaptured != 0) {
            addPiece(sqDst, pcCaptured);
        }
    }

    public static boolean makeMove(int mv) {         // 走一步棋
        long key = zobr.key;
        int pcCaptured = movePiece(mv);
        if (Rule.isChecked()) {
            undoMovePiece(mv, pcCaptured);
            return false;
        }
        changeSide();
        mvsList[nHistoryMoveNum].set(mv, pcCaptured, Rule.isChecked(), key);
        ++Engine.nDistance;
        ++nHistoryMoveNum;
        return true;
    }

    public static void undoMakeMove() { // 撤消走一步棋
        --Engine.nDistance;
        nHistoryMoveNum--;
        changeSide();
        undoMovePiece(mvsList[nHistoryMoveNum].mv, mvsList[nHistoryMoveNum].ucpcCaptured);
    }

    static boolean inCheck() {      // 上一步是否被将军
        return mvsList[nHistoryMoveNum - 1].ucbCheck;
    }

    static boolean captured() {     // 上一步是否吃子
        return mvsList[nHistoryMoveNum - 1].ucpcCaptured != 0;
    }

    static void nullMove() {                       // 走一步空步
        mvsList[nHistoryMoveNum].set(0, 0, false, zobr.key);
        Engine.nDistance++;
        nHistoryMoveNum++;
        changeSide();
    }

    static void undoNullMove() {                   // 撤消走一步空步
        Engine.nDistance--;
        nHistoryMoveNum--;
        changeSide();
    }

    // 检测重复局面
    static int repStatus(int nRecur) {
        boolean bSelfSide, bPerpCheck, bOppPerpCheck;

        bSelfSide = false;
        bPerpCheck = bOppPerpCheck = true;
        int i = 1;
        MoveStruct lpmvs = mvsList[nHistoryMoveNum - i];
        while (lpmvs.mv != 0 && lpmvs.ucpcCaptured == 0) {  //走法存在，且没有吃子
            if (bSelfSide) {
                bPerpCheck = bPerpCheck && lpmvs.ucbCheck;
                if (lpmvs.key == zobr.key) {
                    nRecur--;
                    if (nRecur == 0) {
                        return 1 + (bPerpCheck ? 2 : 0) + (bOppPerpCheck ? 4 : 0);
                    }
                }
            } else {
                bOppPerpCheck = bOppPerpCheck && lpmvs.ucbCheck;
            }
            bSelfSide = !bSelfSide;
            i++;
            lpmvs = mvsList[nHistoryMoveNum - i];
        }
        return 0;
    }

    static int repValue(int nRepStatus) {        // 重复局面分值
        int vlReturn;
        vlReturn = ((nRepStatus & 2) == 0 ? 0 : Engine.nDistance - Engine.MATE_VALUE) + //我方：没将军or将军
                ((nRepStatus & 4) == 0 ? 0 : Engine.MATE_VALUE - Engine.nDistance); //对方：没将军or将军
        return vlReturn == 0 ? -DRAW_VALUE : vlReturn;  //双方：都没将军or有将军
    }

    static boolean nullOkay() {                 // 判断是否允许空步裁剪
        return (sdPlayer == 0 ? Value.vlWhite : Value.vlBlack) > NULL_MARGIN;
    }

    //起始棋局
    public static final short[] startupMap = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 20, 19, 18, 17, 16, 17, 18, 19, 20, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0,
            0, 0, 0, 22, 0, 22, 0, 22, 0, 22, 0, 22, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 14, 0, 14, 0, 14, 0, 14, 0, 14, 0, 0, 0, 0,
            0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 12, 11, 10, 9, 8, 9, 10, 11, 12, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    // 获得格子的纵坐标
    public static int getCoordY(int sq) {
        return sq >> 4;
    }

    // 获得格子的横坐标
    public static int getCoordX(int sq) {
        return sq & 15;
    }

    // 根据横纵坐标获得格子
    public static int getCoordPoint(int x, int y) {
        return x + (y << 4);
    }

    // 中心翻转棋盘（180°）
    public static int centreFlip(int sq) {
        return 254 - sq;
    }

    // 横坐标水平镜像
    public static int horizontalFlip(int x) {
        return 14 - x;
    }

    // 纵坐标垂直镜像
    public static int verticlFlip(int y) {
        return 15 - y;
    }

    // 获得红黑标记(红子是8，黑子是16)
    public static int getSideTag(int sd) {
        return 8 + (sd << 3);
    }

    // 获得对方红黑标记
    public static int getOppositeSideTag(int sd) {
        return 16 - (sd << 3);
    }

    // 获得走法的起点
    public static int getMoveSrc(int mv) {
        return mv & 255;
    }

    // 获得走法的终点
    public static int getMoveDst(int mv) {
        return mv >> 8;
    }

    // 根据起点和终点获得走法
    public static int getMove(int sqSrc, int sqDst) {
        return sqSrc + sqDst * 256;
    }

    // 走法水平镜像
    public static int getFlipMove(int mv) {
        return getMove(centreFlip(getMoveSrc(mv)), centreFlip(getMoveDst(mv)));
    }

}
