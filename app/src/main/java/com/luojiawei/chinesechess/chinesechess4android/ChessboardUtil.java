package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-08-23.
 */
public class ChessboardUtil {
    public static int sdPlayer;                   // 轮到谁走，0=红方，1=黑方
    public static int[] currentMap = new int[256];          // 棋盘上的棋子

    // 棋盘范围
    public final static int BOARD_TOP = 3;
    public final static int BOARD_BOTTOM = 12;
    public final static int BOARD_LEFT = 3;
    public final static int BOARD_RIGHT = 11;

    // 棋子编号
    public final static int PIECE_KING = 0;
    public final static int PIECE_ADVISOR = 1;
    public final static int PIECE_BISHOP = 2;
    public final static int PIECE_KNIGHT = 3;
    public final static int PIECE_ROOK = 4;
    public final static int PIECE_CANNON = 5;
    public final static int PIECE_PAWN = 6;


    public static void startup() {            // 初始化棋盘
        sdPlayer = 0;
        for (int i = 0; i < 256; i++) {
            currentMap[i] = startupMap[i];
        }
    }

    public static void changeSide() {         // 交换走子方
        sdPlayer = 1 - sdPlayer;
    }

    public static void addPiece(int sq, int pc) { // 在棋盘上放一枚棋子
        currentMap[sq] = pc;
    }

    public static void delPiece(int sq) {         // 从棋盘上拿走一枚棋子
        currentMap[sq] = 0;
    }

    public static void movePiece(int mv) {         // 搬一步棋的棋子
        int sqSrc, sqDst, pc;
        sqSrc = getMoveSrc(mv);
        sqDst = getMoveDst(mv);
        delPiece(sqDst);
        pc = currentMap[sqSrc];
        delPiece(sqSrc);
        addPiece(sqDst, pc);
    }

    public static void makeMove(int mv) {         // 走一步棋
        movePiece(mv);
        changeSide();
    }

    //起始棋局
    public static final short[] startupMap = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0,20,19,18,17,16,17,18,19,20, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0,21, 0, 0, 0, 0, 0,21, 0, 0, 0, 0, 0,
            0, 0, 0,22, 0,22, 0,22, 0,22, 0,22, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0,14, 0,14, 0,14, 0,14, 0,14, 0, 0, 0, 0,
            0, 0, 0, 0,13, 0, 0, 0, 0, 0,13, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0,12,11,10, 9, 8, 9,10,11,12, 0, 0, 0, 0,
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

}
