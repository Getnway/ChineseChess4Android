package com.luojiawei.chinesechess.chinesechess4android;

/**
 * Created by L1 on 15-08-23.
 */
public class ChessboardUtil {
    public static int sdPlayer;                   // �ֵ�˭�ߣ�0=�췽��1=�ڷ�
    public static int[] currentMap = new int[256];          // �����ϵ�����

    // ���̷�Χ
    public final static int BOARD_TOP = 3;
    public final static int BOARD_BOTTOM = 12;
    public final static int BOARD_LEFT = 3;
    public final static int BOARD_RIGHT = 11;

    // ���ӱ��
    public final static int PIECE_KING = 0;
    public final static int PIECE_ADVISOR = 1;
    public final static int PIECE_BISHOP = 2;
    public final static int PIECE_KNIGHT = 3;
    public final static int PIECE_ROOK = 4;
    public final static int PIECE_CANNON = 5;
    public final static int PIECE_PAWN = 6;


    public static void startup() {            // ��ʼ������
        sdPlayer = 0;
        for (int i = 0; i < 256; i++) {
            currentMap[i] = startupMap[i];
        }
    }

    public static void changeSide() {         // �������ӷ�
        sdPlayer = 1 - sdPlayer;
    }

    public static void addPiece(int sq, int pc) { // �������Ϸ�һö����
        currentMap[sq] = pc;
    }

    public static void delPiece(int sq) {         // ������������һö����
        currentMap[sq] = 0;
    }

    public static void movePiece(int mv) {         // ��һ���������
        int sqSrc, sqDst, pc;
        sqSrc = getMoveSrc(mv);
        sqDst = getMoveDst(mv);
        delPiece(sqDst);
        pc = currentMap[sqSrc];
        delPiece(sqSrc);
        addPiece(sqDst, pc);
    }

    public static void makeMove(int mv) {         // ��һ����
        movePiece(mv);
        changeSide();
    }

    //��ʼ���
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

    // ��ø��ӵ�������
    public static int getCoordY(int sq) {
        return sq >> 4;
    }

    // ��ø��ӵĺ�����
    public static int getCoordX(int sq) {
        return sq & 15;
    }

    // ���ݺ��������ø���
    public static int getCoordPoint(int x, int y) {
        return x + (y << 4);
    }

    // ���ķ�ת���̣�180�㣩
    public static int centreFlip(int sq) {
        return 254 - sq;
    }

    // ������ˮƽ����
    public static int horizontalFlip(int x) {
        return 14 - x;
    }

    // �����괹ֱ����
    public static int verticlFlip(int y) {
        return 15 - y;
    }

    // ��ú�ڱ��(������8��������16)
    public static int getSideTag(int sd) {
        return 8 + (sd << 3);
    }

    // ��öԷ���ڱ��
    public static int getOppositeSideTag(int sd) {
        return 16 - (sd << 3);
    }

    // ����߷������
    public static int getMoveSrc(int mv) {
        return mv & 255;
    }

    // ����߷����յ�
    public static int getMoveDst(int mv) {
        return mv >> 8;
    }

    // ���������յ����߷�
    public static int getMove(int sqSrc, int sqDst) {
        return sqSrc + sqDst * 256;
    }

}
